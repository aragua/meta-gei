require recipes-sato/images/core-image-sato.bb

IMAGE_INSTALL += "htop netconfig example"

EXTRA_IMAGE_FEATURES += "ssh-server-openssh"