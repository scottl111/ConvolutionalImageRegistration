/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
#include <iostream>
#include <jni.h>
#include "org_sikuli_script_VDictProxy.h"
#include "vdict.cpp"

using namespace std;


/*
 * Class:     org_sikuli_script_VDictProxy
 * Method:    getInstance
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_sikuli_script_VDictProxy_getInstance
  (JNIEnv *env, jobject jobj){
  VizDict* inst = new VizDict();
  return (jlong)inst;
}

/*
 * Class:     org_sikuli_script_VDictProxy
 * Method:    _insert
 * Signature: (JLjava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_org_sikuli_script_VDictProxy__1insert
  (JNIEnv *env, jobject jobj, jlong inst, jstring jkey, jint jval){
  VizDict* dict = (VizDict *)inst;
  const char *key = env->GetStringUTFChars(jkey, NULL);
  dict->insert(key, jval);
}

/*
 * Class:     org_sikuli_script_VDictProxy
 * Method:    _lookup
 * Signature: (JLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_sikuli_script_VDictProxy__1lookup
  (JNIEnv * env, jobject jobj, jlong inst, jstring jkey){
  VizDict* dict = (VizDict *)inst;
  const char *key = env->GetStringUTFChars(jkey, NULL);
  return dict->lookup(key);
}

/*
 * Class:     org_sikuli_script_VDictProxy
 * Method:    _lookup_similar
 * Signature: (JLjava/lang/String;F)I
 */
JNIEXPORT jint JNICALL Java_org_sikuli_script_VDictProxy__1lookup_1similar
  (JNIEnv *env, jobject jobj, jlong inst, jstring jkey, jdouble similarity){
  VizDict* dict = (VizDict *)inst;
  const char *key = env->GetStringUTFChars(jkey, NULL);
  return dict->lookup_similar(key, similarity);
}

/*
 * Class:     org_sikuli_script_VDictProxy
 * Method:    _lookup_similar_n
 * Signature: (JLjava/lang/String;DI)[I
 */
JNIEXPORT jintArray JNICALL Java_org_sikuli_script_VDictProxy__1lookup_1similar_1n
  (JNIEnv *env, jobject jobj, jlong inst, jstring jkey, jdouble similarity, jint n){
  VizDict* dict = (VizDict *)inst;
  const char *key = env->GetStringUTFChars(jkey, NULL);
  vector<int> values = dict->lookup_similar_n(key, similarity, n);
  int size = values.size();
  jintArray ret = (jintArray)env->NewIntArray(size);
  int *t = new int[size];
  for(int i=0;i<size;i++) t[i] = values[i];
  env->SetIntArrayRegion( ret, 0, size, (jint *)t);
  delete [] t;
  return ret;
}

/*
 * Class:     org_sikuli_script_VDictProxy
 * Method:    _erase
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_sikuli_script_VDictProxy__1erase
  (JNIEnv *env, jobject jobj, jlong inst, jstring jkey){
  VizDict* dict = (VizDict *)inst;
  const char *key = env->GetStringUTFChars(jkey, NULL);
  return dict->erase(key);
}


/*
 * Class:     org_sikuli_script_VDictProxy
 * Method:    _size
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_sikuli_script_VDictProxy__1size
  (JNIEnv *env, jobject jobj, jlong inst){
  VizDict* dict = (VizDict *)inst;
  return dict->size();
}

/*
 * Class:     org_sikuli_script_VDictProxy
 * Method:    _empty
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_org_sikuli_script_VDictProxy__1empty
  (JNIEnv *env, jobject jobj, jlong inst){
  VizDict* dict = (VizDict *)inst;
  return dict->empty();
}
