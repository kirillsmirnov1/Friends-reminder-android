/home/kirill/Android/Sdk/emulator/emulator -avd Nexus_4_API_21 & adb wait-for-device;

echo 'emulator is up and running'

rm -r fastlane/metadata/
bundle exec fastlane capture_screen