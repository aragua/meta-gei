# ENABLE CAN
do_deploy_append() {
if [ "${ENABLE_CAN}" = "1" ]; then
echo "# Enable CAN" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
echo "dtoverlay=mcp2515-can0,oscillator=16000000,interrupt=25" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
fi
}