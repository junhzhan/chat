#!/bin/sh

BUILD_VARIANT=RemoteDebug
OUTPUT=app-remote-debug.apk
pwd
cd mckinley
if [ -f app/build/outputs/apk/$OUTPUT ];then
    rm app/build/outputs/apk/$OUTPUT
fi
./gradlew --stacktrace assembleRemoteDebug
if [ ! -f app/build/outputs/apk/$OUTPUT ];then
    exit 1
fi
cp app/build/outputs/apk/$OUTPUT ../
cd ..
python makeDiagnoseJar.py $OUTPUT $1
if [ ! -f $1.jar ];then
    echo "generate fail"
else
    echo "generate succeed"
fi
