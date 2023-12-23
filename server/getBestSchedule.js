const functions = require('firebase-functions')
const admin = require('firebase-admin')
const serviceAccount = require("./admin.json")
const { getFirestore } = require('firebase-admin/firestore')

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

const db = getFirestore();
const prefix = '2023-01-01 ';

function getBeginIndex(minDate, beginDate, row) {
    const hour = (beginDate.getTime() - minDate.getTime())/(60*60*1000);
    const index = Math.floor(2 * hour);
    console.log(`[Begin] hour: ${hour.toString()}, index: ${index.toString()}`);
    if (index >= 0 && index < row) {
        return index;
    } else if (index < 0) {
        console.log(`[Begin] -> index: 0}`);
        return 0;
    } else {
        console.log(`Wrong start time: start index out of range: ${index}/${row}`);
    }
}

function getEndIndex(minDate, endDate, row) {
    const hour = (endDate.getTime() - minDate.getTime())/(60*60*1000);
    const index = Math.ceil(2 * hour - 1);
    console.log(`[End] hour: ${hour.toString()}, index: ${index.toString()}`);
    if (index >= 0 && index < row) {
        return index;
    } else if (index >= row) {
        console.log(`[End] -> index: ${row-1}}`);
        return row-1;
    } else {
        console.log(`Wrong end time: end index out of range: ${index}/${row}`);
    }
}

function getBeginTimeFromIndex(minDate, index) {
    var result = new Date(minDate);
    result.setMinutes(minDate.getMinutes() + 30*index);
    return `${result.getHours()}:${result.getMinutes()}`;
}

function getEndTimeFromIndex(minDate, index) {
    var result = new Date(minDate);
    result.setMinutes(minDate.getMinutes() + 30*(index+1));
    return `${result.getHours()}:${result.getMinutes()}`;
}

class TimeBlock {
    constructor(date, start, end, userList) {
        this.date = date;
        this.start = start;
        this.end = end;
        this.userList = userList;
    }

    connectedTo = (prev) => {
        if (this.date != prev.date || this.start - prev.end != 1) return false;
        return this.userList.length === prev.userList.length && this.userList.every((v, i) => v === prev.userList[i]);
    }

    mergeWith = (prev) => new TimeBlock(this.date, prev.start, this.end, this.userList);
}

async function markTable(group) {
    const dateList = [];
    const dates = group.dates;
    const members = group.users;
    // Javascript는 날짜가 포함되어야 Date 객체를 사용가능하므로 불필요한 정보이지만 날짜는 2023-01-01로 고정
    const minDate = new Date(prefix + group.startTime);
    const maxDate = new Date(prefix + group.endTime);
    const row = 2 * (maxDate.getTime() - minDate.getTime()) / (60 * 60 * 1000);
    const col = members.length;

    await Promise.all(dates.map(async date => {
        const timeTable = Array.from(Array(row), () => Array(col).fill(1));

        for (var mIdx = 0; mIdx < col; mIdx++) {
            var uid = members[mIdx];
            console.log(`uid: ${uid}, date: ${date}`);
            const schedulesRef = db.collection('users').doc(uid).collection('schedules').where('date', '==', date);

            try {
                const schedule = await schedulesRef.get();
                schedule.forEach(s => {
                    console.log(`schedule: ${JSON.stringify(s)}`);
                    console.log(`startTime: ${s.data().startTime}`);
                    console.log(`endTime: ${s.data().endTime}`);
                    const beginIdx = getBeginIndex(minDate, new Date(prefix + s.data().startTime), row);
                    const endIdx = getEndIndex(minDate, new Date(prefix + s.data().endTime), row);

                    for (var tIdx = beginIdx; tIdx <= endIdx; tIdx++) {
                        timeTable[tIdx][mIdx] = 0;
                    }
                });
            } catch (error) {
                console.error('일정을 가져오는 중 오류 발생:', error);
                throw error;
            }
        }

        const timeBlocks = [];
        for (var tIdx = 0; tIdx < row; tIdx++) {
            var users = [];
            for (var mIdx = 0; mIdx < col; mIdx++) {
                if (timeTable[tIdx][mIdx] == 1) {
                    users.push(mIdx);
                }
            }
            timeBlocks.push(new TimeBlock(date, tIdx, tIdx, users));
        }

        const mergeResult = new Array();
        var prev = timeBlocks[0];
        for (var tIdx = 1; tIdx < row; tIdx++) {
            var timeBlock = timeBlocks[tIdx];
            if (timeBlock.connectedTo(prev)){
                prev = timeBlock.mergeWith(prev);
            } else {
                mergeResult.push(prev);
                prev = timeBlocks[tIdx];
            }
        }
        mergeResult.push(prev);

        // 인원수 기준으로 내림차순 정렬
        // mergeResult.sort((block1, block2) => block2.userList.length - block1.userList.length);

        // 전원 참여 가능한 시간대만 필터링
        const filteringResult = mergeResult.filter(block => block.userList.length == col);

        if (filteringResult.length > 5)
        filteringResult = filteringResult.slice(6)

        filteringResult.forEach (async (block) => {
            console.log(JSON.stringify(block));
            const result = {
                "date": block.date,
                "startTime": getBeginTimeFromIndex(minDate, block.start),
                "endTime": getEndTimeFromIndex(minDate, block.end),
                "number": block.userList.length,
                "members": Array.from(block.userList, index => members[index])
            };
            dateList.push(result);
        });
    }))

    return dateList;
}

async function calculateTimes(ownerId, groupId) {
    const groupRef = db.collection('users').doc(ownerId).collection('promises').doc(groupId);
    try {
        const result = await groupRef.get();
        const group = result.data();
        console.log(`data: ${JSON.stringify(group)}`);
        return await markTable(group);
    } catch(error){
        console.error('데이터를 가져오는 중 오류 발생:', error);
        throw error;
    };
}

exports.getBestSchedule = functions.region('asia-northeast3').https.onCall(async (data) => {
    const ownerId = data.ownerId;
    const groupId = data.groupId;
    console.log(`ownerId: ${ownerId}, groupId: ${groupId}`)

    const timesArray = await calculateTimes(ownerId, groupId);
    return {
        'number': timesArray.length,
        'times': timesArray
    };
});

