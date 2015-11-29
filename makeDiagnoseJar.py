#!/usr/bin/env python3
import sys
import os

apkfile = sys.argv[1]
userid = sys.argv[2]
dirname = os.path.dirname(apkfile)
if not dirname == '':
    apk = os.path.basename(apkfile)
else:
    apk = apkfile
print('apk name is ', apk)
if apk == '' or not apk.endswith('.apk'):
    sys.exit()
os.system('./apktool d -f ' + apkfile)
apkname = apk[:apk.find('.apk')]
if os.path.isdir(apkname):
    print('apktool decompile success')
else:
    print('apktool decompile fail')
    sys.exit()
smalifile = os.path.join(apkname, 'smali', 'com', 'cootek', 'smartdialer', 'xcode', 'DynamicCode.smali')
print('target smali file is ', smalifile)
os.system('java -jar libs/smali-2.1.0.jar -o classes.dex ' + smalifile)
if os.path.exists('classes.dex'):
    print('recompile to dex success')
else:
    print('recompile to dex fail')

output_jar = userid + '.jar'
os.system('./dx --dex --output=' + output_jar + ' classes.dex')
if os.path.exists(output_jar):
    print('make output jar file success')
else:
    print('make output jar file fail')
