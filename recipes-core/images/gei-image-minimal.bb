require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL += "htop netconfig example"

EXTRA_IMAGE_FEATURES += "ssh-server-openssh"