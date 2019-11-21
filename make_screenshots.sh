/home/kirill/Android/Sdk/emulator/emulator -avd Nexus_4_API_21 & adb wait-for-device;

adb shell date -s 20191113.123400

echo 'emulator is up and running'

bundle exec fastlane capture_screen

rm Screenshots/screenshots.html