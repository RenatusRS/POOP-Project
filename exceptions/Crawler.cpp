#include "Crawler.h"
#include <string>
#include <iostream>

#define CURL_STATICLIB
#include <curl/curl.h>

using namespace std;

size_t WriteCallback(void* contents, size_t size, size_t nmemb, void* userp) {
    ((std::string*)userp)->append((char*)contents, size * nmemb);
    return size * nmemb;
}

std::string download(std::string link) {
    CURL* curl;
    CURLcode res;
    std::string readBuffer;

    curl = curl_easy_init();
    curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, 0);
    curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0);
    if (curl) {
        res = curl_easy_setopt(curl, CURLOPT_URL, link.c_str());
        if (res != CURLE_OK) {
            std::cout << "curl_easy_perform()failed: \n" << curl_easy_strerror(res) << std::endl;
        }

        res = curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
        if (res != CURLE_OK) {
            std::cout << "curl_easy_perform()failed: \n" << curl_easy_strerror(res) << std::endl;
        }

        res = curl_easy_setopt(curl, CURLOPT_WRITEDATA, &readBuffer);
        if (res != CURLE_OK) {
            std::cout << "curl_easy_perform()failed: \n" << curl_easy_strerror(res) << std::endl;
        }

        res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            std::cout << "curl_easy_perform()failed: \n" << curl_easy_strerror(res) << std::endl;
        }

        curl_easy_cleanup(curl);
    }

    //std::cout << readBuffer << std::endl;
    return readBuffer;
}

string JStringToString(JNIEnv* env, jstring jStr) {
    if (!jStr) return "";

    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray)env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));

    size_t length = (size_t)env->GetArrayLength(stringJbytes);
    jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    std::string ret = std::string((char*)pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

JNIEXPORT jstring JNICALL Java_Crawler_crawler (JNIEnv* env, jobject, jstring url) {
    return env->NewStringUTF(download(JStringToString(env, url)).c_str());
};