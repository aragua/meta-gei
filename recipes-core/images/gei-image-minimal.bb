require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL += "htop netconfig example kernel-modules libsocketcan canutils udev-rules bluez5 wpa-supplicant iw linux-firmware hostapd"

EXTRA_IMAGE_FEATURES += "ssh-server-openssh package-management"
