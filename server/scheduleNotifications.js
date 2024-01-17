/* eslint-disable max-len */
/* eslint-disable require-jsdoc */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const serviceAccount = require("./admin.json");
const {getFirestore} = require("firebase-admin/firestore");

const NOTIFICATION_OFFSET = 60;

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = getFirestore();

async function sendPushNotification(token, title, body) {
  const message = {
    notification: {
      title: title,
      body: body,
    },
    token: token,
  };

  try {
    const response = await admin.messaging().send(message);
    console.log("Successfully sent message:", response);
  } catch (error) {
    console.error("Error sending message:", error);
  }
}

function getDateInSeoulTimeZone() {
  return new Date(new Date().toLocaleString("en-US", {timeZone: "Asia/Seoul"}));
}

function isWithin1Hour(time1, time2) {
  const differenceInMinutes = (time1 - time2) / (1000 * 60);
  return differenceInMinutes <= 60;
}

async function processUser(userDoc) {
  console.log("---");
  console.log(`Processing user: ${userDoc.id}`);

  const userData = userDoc.data();
  const fcmToken = userData.fcmToken;

  if (fcmToken) {
    console.log(`Processing FCM: User ${userDoc.id} has fcmToken`);
    const now = getDateInSeoulTimeZone();
    const nowDateOnly = now.toISOString().split("T")[0];

    // 필요한 필드만 선택하여 Firestore에서 스케줄 가져오기
    const schedulesSnapshot = await userDoc.ref
        .collection("schedules")
        .where("date", "==", nowDateOnly)
        .select("date", "startTime", "notified", "name", "place", "endTime")
        .get();

    // Promise.all을 사용하여 병렬로 각 일정 처리
    await Promise.all(schedulesSnapshot.docs.map((scheduleDoc) =>
      processSchedules(scheduleDoc, now, fcmToken, userDoc.ref.collection("schedules"), userDoc.id),
    ));
  }
}

async function processSchedules(scheduleDoc, now, fcmToken, schedulesCollection, userId) {
  console.log(`Processing schedule: ${scheduleDoc.id}`);
  const scheduleData = scheduleDoc.data();
  const startTime = getStartTime(scheduleData.date, scheduleData.startTime);
  const notificationTime = new Date(startTime);
  notificationTime.setMinutes(notificationTime.getMinutes() - NOTIFICATION_OFFSET);
  console.log(`[now] ${now}`);
  console.log(`[notification time] ${notificationTime}`);

  if (isWithin1Hour(now, notificationTime) && !scheduleData.notified) {
    const title = `${scheduleData.name} (${scheduleData.place})`;
    const body = `${scheduleData.startTime} ~ ${scheduleData.endTime}`;
    console.log(`Sending notifications for user: ${userId}`);
    console.log(`Schedule details - Name: ${scheduleData.name}, Place: ${scheduleData.place}, StartTime: ${scheduleData.startTime}, EndTime: ${scheduleData.endTime}`);

    const batch = db.batch();
    batch.update(scheduleDoc.ref, {notified: true});

    await batch.commit();
    await sendPushNotification(fcmToken, title, body);
  } else {
    console.log("Notification skipped for past or current event");
  }
}

function getStartTime(date, time) {
  return new Date(new Date(date).setHours(parseInt(time.split(":")[0], 10), parseInt(time.split(":")[1], 10)));
}

// 실행 시간 측정 시작
console.time("scheduleNotification");
exports.scheduleNotifications = functions.pubsub.schedule("0 * * * *").timeZone("Asia/Seoul").onRun(async (context) => {
  console.log("sendTimeNotifications function started");

  try {
    const usersSnapshot = await db.collection("users").get();
    await Promise.all(usersSnapshot.docs.map((userDoc) =>
      processUser(userDoc),
    ));
  } catch (error) {
    console.error("Error getting users from Firestore:", error);
  }
  // scheduleNotifications 함수의 실행 시간 측정 종료
  console.timeEnd("scheduleNotification");
});
