FILESEXTRAPATHS_prepend := "${THISDIR}/wpa-supplicant:"

SRC_URI += " file://wpa_supplicant-wlan0.conf "

do_install_append () {
	install -d ${D}${sysconfdir}/wpa_supplicant/
	install -m 600 ${WORKDIR}/wpa_supplicant-wlan0.conf ${D}${sysconfdir}/wpa_supplicant/
	install -d ${D}/${systemd_unitdir}/system/multi-user.target.wants/
	ln -s ../wpa_supplicant@.service ${D}/${systemd_unitdir}/system/multi-user.target.wants/wpa_supplicant@wlan0.service
}

FILES_${PN} += " ${systemd_unitdir}/system/multi-user.target.wants/ "
