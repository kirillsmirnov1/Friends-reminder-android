/home/kirill/Android/Sdk/emulator/emulator -avd Nexus_4_API_21 & adb wait-for-device;

adb shell date -s 20191113.123400

echo 'emulator is up and running'

bundle exec fastlane capture_screen

rm Screenshots/screenshots.html

cd Screenshots/phoneScreenshots

convert en_journal.png -resize 25% ../en_journal.png
convert en_friend.png -resize 25% ../en_friend.png
convert en_trackers.png -resize 25% ../en_trackers.png

cd ../..