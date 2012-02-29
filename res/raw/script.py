import sys
sys.stderr = open('/sdcard/dht.err', 'w')
sys.stdout = open('/sdcard/dht.out', 'w')

import android,time
import dht

droid = android.Android()
droid.makeToast('Python says: Hello, Android!')
droid.vibrate(300)

dht.SwiftTraker(9999).start()
