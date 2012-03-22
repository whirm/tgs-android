import sys
sys.stderr = open('/sdcard/swift/dht.err', 'w')
sys.stdout = open('/sdcard/swift/dht.out', 'w')

import android,time
import dht

droid = android.Android()
droid.makeToast('Python says: Hello, Android!')
droid.vibrate(300)

dht.SwiftTraker(9999).start()
# Raul, 2012-03-09: SwiftTracker does not create a thread!!
