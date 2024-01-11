/* eslint-disable max-len */
/* eslint-disable require-jsdoc */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const serviceAccount = require("./admin.json");
const {getFirestore} = require("firebase-admin/firestore");

const NOTIFICATION_OFFSET = 15;

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

function isWithin15Minutes(time1, time2) {
  const differenceInMinutes = (time1 - time2) / (1000 * 60);
  return differenceInMinutes <= 15;
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
    const nextDay = new Date(now);
    nextDay.setDate(nextDay.getDate() + 1);
    const nextDateOnly = nextDay.toISOString().split("T")[0];

    const schedulesSnapshot = await userDoc.ref.collection("schedules").where("date", "in", [nowDateOnly, nextDateOnly]).get();
    await processSchedules(schedulesSnapshot, now, fcmToken, userDoc);
  }
}

async function processSchedules(schedulesSnapshot, now, fcmToken, userDoc) {
  for (const scheduleDoc of schedulesSnapshot.docs) {
    console.log(`Processing schedule: ${scheduleDoc.id}`);
    const scheduleData = scheduleDoc.data();
    const startTime = getStartTime(scheduleData.date, scheduleData.startTime);
    const notificationTime = new Date(startTime);
    notificationTime.setMinutes(notificationTime.getMinutes() - NOTIFICATION_OFFSET);
    console.log(`[now] ${now}`);
    console.log(`[notification time] ${notificationTime}`);

    if (isWithin15Minutes(now, notificationTime) && !scheduleData.notified) {
      const title = `${scheduleData.name} (${scheduleData.place})`;
      const body = `${scheduleData.startTime} ~ ${scheduleData.endTime}`;
      console.log(`Sending notifications for user: ${userDoc.id}`);
      console.log(`Schedule details - Name: ${scheduleData.name}, Place: ${scheduleData.place}, StartTime: ${scheduleData.startTime}, EndTime: ${scheduleData.endTime}`);
      await sendPushNotification(fcmToken, title, body);

      // 일정에 대한 알림을 보냈으므로 Firestore에서 상태를 업데이트
      await scheduleDoc.ref.update({notified: true});
    } else {
      console.log("Notification skipped for past or current event");
    }
  }
}

function getStartTime(date, time) {
  return new Date(new Date(date).setHours(parseInt(time.split(":")[0], 10), parseInt(time.split(":")[1], 10)));
}

// 크롬 식을 통해 10분마다 리마인드할 일정이 있는지 확인함
exports.scheduleNotifications = functions.pubsub.schedule("*/5 * * * *").timeZone("Asia/Seoul").onRun(async (context) => {
  console.log("sendTimeNotifications function started");
  try {
    const usersSnapshot = await db.collection("users").get();
    for (const userDoc of usersSnapshot.docs) {
      await processUser(userDoc);
    }
  } catch (error) {
    console.error("Error getting users from Firestore:", error);
  }
});
