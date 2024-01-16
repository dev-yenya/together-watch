import * as functions from "firebase-functions"
import admin from 'firebase-admin'
import serviceAccount from "./admin.json";
import { FieldValue, getFirestore } from 'firebase-admin/firestore';

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount as admin.ServiceAccount)
});

const db = getFirestore();

const deleteUser = async (uid: string) => {
    const batch = db.batch();
    const userRef = db.collection('users').doc(uid);

    await userRef.collection('promises').listDocuments().then(val => {
        val.map((doc) => batch.delete(doc));
    }).catch((error) => {
        console.error(`promises collection 삭제 실패: ${error.message}`)
    });

    await userRef.collection('schedules').listDocuments().then(val => {
        val.map((doc) => batch.delete(doc));
    }).catch((error) => {
        console.error(`schedules collection 삭제 실패: ${error.message}`)
    });

    batch.commit();

    await admin.auth().deleteUser(uid).then(() => {
        console.log(`${uid} auth user 삭제 성공`);
    }).catch((error: Error) =>
        console.error(`auth user 삭제 실패: ${error.message}`)
    );
    await userRef.delete().then(() => {
        console.error(`user document 삭제 성공`);
    }).catch((error: Error) => {
        console.error(`user document 삭제 실패: ${error.message}`);
    });
};

const deleteParticipant = async (uid: string) => {
    const querySnapshot = await db.collectionGroup("promises").where('users', 'array-contains', uid).get();
    querySnapshot.forEach((doc) => {
//        console.log(doc.id, ' => ', doc.data());
        doc.ref.update({
            users: FieldValue.arrayRemove(uid)
        });
        console.log(`${uid} 참가자 목록에서 삭제. 참여 약속 개수만큼 출력되는 로그`);
    });
    return uid;
};

const quitService = async (uid: string) => {
    await deleteParticipant(uid).then((uid:string) => {
        return deleteUser(uid);
    });
};

export const quit = functions.region('asia-northeast3').https
    .onCall((data) => {
        const uid: string = data.uid;
        console.log(`uid: ${uid}`);
        return quitService(uid);
    });