const functions = require('firebase-functions')
const admin = require('firebase-admin')
const serviceAccount = require("./admin.json")
const axios = require('axios')
const {getFirestore} = require('firebase-admin/firestore')

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

const db = getFirestore();
const kakaoRequestMeUrl = 'https://kapi.kakao.com/v2/user/me'

function requestMe(kakaoAccessToken) {
    console.log('Requesting user profile from Kakao API server.');

    return axios.get(
        kakaoRequestMeUrl, {
            headers: {'Authorization': 'Bearer ' + kakaoAccessToken}
        }
    ).catch(function(error) {
        console.log(error);
    });
}

function updateOrCreateUser(userId, displayName, photoURL) {
    console.log('updating or creating a firebase user');
    const updateParams = {
        provider: 'KAKAO',
        displayName: displayName,
    };
    if (displayName) {
        updateParams['displayName'] = displayName;
    }
    if (photoURL) {
        updateParams['photoURL'] = photoURL;
    }
    console.log(updateParams);
    return admin.auth().updateUser(userId, updateParams)
        .catch((error) => {
            if (error.code === 'auth/user-not-found') {
                updateParams['uid'] = userId;
                const user = {
                    uid: updateParams['uid'],
                    displayName: updateParams['displayName'],
                    photoURL: updateParams['photoURL']
                };
                db.collection('users').doc(userId).set(user);
                return admin.auth().createUser(updateParams);
            }
            throw error;
        });
}

function createFirebaseToken(kakaoAccessToken) {
    return requestMe(kakaoAccessToken).then((response) => {
        const body = response.data;
        const userId = `kakao:${body.id}`
        if (!userId) {
            throw new functions.https.HttpsError('invalid-argument', 'Not response: Failed get userId');
        }

        let nickname = null
        let profileImage = null
        if (body.properties) {
            nickname = body.properties.nickname
            profileImage = body.properties.profile_image
        }
        return updateOrCreateUser(userId, nickname, profileImage)
    }).then((userRecord) => {
        const uid = userRecord.uid;
        console.log(`creating a custom firebase token based on uid ${uid}`)
        return admin.auth().createCustomToken(uid, { provider: 'KAKAO' })
    });
}

exports.kakaoCustomAuth = functions.region('asia-northeast3').https
    .onCall((data) => {
        const token = data.token

        if (!(typeof token === 'string') || token.length === 0) {
            throw new functions.https.HttpsError('invalid-argument', 'The function must be called with one arguments "data" containing the token to add.');
        }

        console.log(`Verifying Kakao token: ${token}`);

        return createFirebaseToken(token)
        .then((firebaseToken) => {
            console.log(`Returning firebase token to user: ${firebaseToken}`)
            return { "custom_token": firebaseToken };
        });
});