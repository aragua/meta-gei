require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL += "htop netconfig example kernel-modules libsocketcan canutils dnsmasq udev-rules bluez5 wpa-supplicant"

EXTRA_IMAGE_FEATURES += "ssh-server-openssh"
