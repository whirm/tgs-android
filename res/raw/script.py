import sys
sys.stderr = open('/sdcard/dht.err', 'w')
sys.stdout = open('/sdcard/dht.out', 'w')

import android,time
import dht

droid = android.Android()
droid.makeToast('Python says: Hello, Android!')
droid.vibrate(300)

dht.SwiftTraker(9999).start()
# Raul, 2012-03-09: SwiftTracker does not create a thread!!
droid.makeToast('SCRIPT.PY: SwiftTracker running')
while 0:
    time.sleep(5)
    droid.makeToast('Why is this never shown? No real thread?')
