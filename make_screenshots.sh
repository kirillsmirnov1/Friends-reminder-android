/home/kirill/Android/Sdk/emulator/emulator -avd Nexus_4_API_21 & disown;

echo 'emulator started'

rm -r fastlane/metadata/
bundle exec fastlane capture_screen