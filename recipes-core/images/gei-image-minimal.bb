require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL += "htop netconfig example kernel-modules libsocketcan canutils"

EXTRA_IMAGE_FEATURES += "ssh-server-openssh"