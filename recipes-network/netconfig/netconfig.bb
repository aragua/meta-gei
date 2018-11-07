SUMMARY = "Systemd network configuration"
DESCRIPTION = "Scripts and configuration files to set up networking on server."
SECTION = "console/network"
LICENSE="CLOSED"

REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI = "file://ethernet.network \
	   file://wifi.network"

NET_IFACE ?= "eth0"

inherit systemd allarch
RDEPENDS_${PN} += "systemd"

do_install () {
      install -d ${D}${systemd_unitdir}/network
      install -m 0644 ${WORKDIR}/ethernet.network ${D}${systemd_unitdir}/network/
      sed -i -e "s:@@interface@@:${NET_IFACE}:" ${D}${systemd_unitdir}/network/ethernet.network
      install -m 0644 ${WORKDIR}/wifi.network ${D}${systemd_unitdir}/network/
}

FILES_${PN} = " \
	    ${systemd_unitdir}/* \
"
