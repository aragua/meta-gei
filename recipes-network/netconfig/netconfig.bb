SUMMARY = "Systemd network configuration"
DESCRIPTION = "Scripts and configuration files to set up networking on server."
SECTION = "console/network"
LICENSE="CLOSED"

REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI = "file://ethernet.network"

NET_IFACE ?= "eth0"

inherit systemd allarch
RDEPENDS_${PN} += "systemd"

do_install () {
      install -d ${D}${sysconfdir}/systemd/network
      install -m 0644 ${WORKDIR}/ethernet.network ${D}${sysconfdir}/systemd/network/
      sed -i -e "s:@@interface@@:${NET_IFACE}:" ${D}${sysconfdir}/systemd/network/ethernet.network
}

FILES_${PN} = " \
	    ${sysconfdir}/* \
"
