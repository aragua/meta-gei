| Creer un compte et se connecter |                  utiliser votre machine                                        |
|---------------------------------|--------------------------------------------------------------------------------|
|- ssh ?user?@geitp-dimer2        |  - il faut installer quelque paquet auparavant (cf google yocto prerequisite)  |
|                                 |     http://www.yoctoproject.org/docs/2.4/mega-manual/mega-manual.html#packages |

---------------------------------------
Pour la premiere utilisation seulement:
---------------------------------------

- On recupere les soures
  * `git clone -b rocko git://git.yoctoproject.org/poky.git`
  * `cd poky`
  * `git clone -b rocko git://git.openembedded.org/meta-openembedded`
  * `git clone -b rocko git://git.yoctoproject.org/meta-raspberrypi`
  * `git clone -b rocko https://github.com/aragua/meta-gei.git`

```bash
  source ./oe-init-build-env
```

- il vous faut configurer dans conf/
  - local.conf
	- `INHERIT += " rm_work "`
	- `MACHINE = "raspberrypi3"`
	- `ENABLE_UART = "1"`
  - bblayers.conf : ajouter les layers manquants
	- `??/poky/meta-openembedded/meta-oe`
	- `??/poky/meta-openembedded/meta-python (really????)`
	- `??/poky/meta-raspberrypi`

---------------
À chaque build:
---------------

Dans poky/ on source l'environnement si ce n est pas deja fait et on lance le build.
```bash
     $ source ./oe-init-build-env
     $ bitbake core-image-minimal
                   ^ nom de l'image a construire.
		   il y a une recette (.bb) associé.

     $ bitbake core-image-sato -> image avec interface graphique
```
Images disponibles:
http://www.yoctoproject.org/docs/2.4/mega-manual/mega-manual.html#ref-images

Pendant la compilation, jeter un coup d'oeil au different layer.
Notamment la doc de meta-raspberrypi

-------
Flasher
-------

Les images produites se trouvent dans :
    tmp/deploy/images/raspberrypi3/core-image-minimal-raspberrypi3.rpi-sdimg
	                              /core-image-sato-raspberrypi3.rpi-sdimg

Faire un dd (man dd) de l'image vers la carte µSD.

Se connecter a la target via lien serie (minicom

----------------
Personnalisation
----------------

1. Creer son propre layer
-------------------------

http://www.yoctoproject.org/docs/2.4/mega-manual/mega-manual.html#creating-a-general-layer-using-the-yocto-layer-script

```
adminlocal@geitp-dimer1:~/poky/build$ yocto-layer create meta-gei -o ..

layer output dir already exists, exiting. (..)
adminlocal@geitp-dimer1:~/poky/build$ yocto-layer create meta-gei -o ../meta-gei
Please enter the layer priority you'd like to use for the layer: [default: 6]
Would you like to have an example recipe created? (y/n) [default: n] y
Please enter the name you'd like to use for your example recipe: [default: example]
Would you like to have an example bbappend file created? (y/n) [default: n] y
Please enter the name you'd like to use for your bbappend file: [default: example]
Please enter the version number you'd like to use for your bbappend file (this should match the recipe you're appending to): [default: 0.1]

New layer created in ../meta-gei.

Don't forget to add it to your BBLAYERS (for details see ../meta-gei/README).

adminlocal@geitp-dimer1:~/poky/build$ tree ../meta-gei/
../meta-gei/
├── conf
│   └── layer.conf
├── COPYING.MIT
├── README
├── recipes-example
│   └── example
│       ├── example-0.1
│       │   ├── example.patch
│       │   └── helloworld.c
│       └── example_0.1.bb
└── recipes-example-bbappend
    └── example-bbappend
            ├── example-0.1
	            │   └── example.patch
		            └── example_0.1.bbappend

7 directories, 8 files

adminlocal@geitp-dimer1:~/poky/build$ bitbake example
# doit construire le paquet example pour notre target
```

2. Creer une "distro" basé sur poky
-----------------------------------

```
$ mkdir meta-gei/conf/distro
$ cat ../meta-gei/conf/distro/gei.conf
require conf/distro/poky.conf
$
```

dans local.conf, mettre DISTRO="gei"
recompiler votre image

3. Utiliser systemd comme systeme d'init
----------------------------------------

http://www.yoctoproject.org/docs/2.4/mega-manual/mega-manual.html#using-systemd-exclusively

```
$ cat ../meta-gei/conf/distro/gei.conf
require conf/distro/poky.conf

DISTRO_FEATURES_append = " systemd"
VIRTUAL-RUNTIME_init_manager = "systemd"

DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"

VIRTUAL-RUNTIME_initscripts = ""
$
```

4. Creer votre image
--------------------

```
$ mkdir ../meta-gei/recipes-core/images/
$ cat  ../meta-gei/recipes-core/images/my_image.bb

require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL += "htop"
```

5. Ajouter un serveur SSH et configurer le reseau
-------------------------------------------------

```
$ cat  ../meta-gei/recipes-core/images/gei-image.bb

require recipes-core/images/core-image-{minimal,sato}.bb

IMAGE_INSTALL += "htop"

EXTRA_IMAGE_FEATURES += "ssh-server-openssh"

$ mkdir ../meta-gei/recipes-network/netconfig/netconfig

$ cat ../meta-gei/recipes-network/netconfig/netconfig/ethernet.network
[Match]
Name=@@interface@@

[Network]
DHCP=ipv4
LinkLocalAddressing=no
IPv6AcceptRouterAdvertisements=no

[DHCP]
ClientIdentifier=mac

$ cat ../meta-gei/recipes-network/netconfig/netconfig.bb
SUMMARY = "Systemd network configuration"
DESCRIPTION = "Scripts and configuration files to set up networking on server."
SECTION = "console/network"
LICENSE="CLOSED"

REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI = "file://ethernet.network"

NET_IFACE ?= "eth0"

inherit systemd allarch

do_install () {
	      install -d ${D}${sysconfdir}/systemd/network
              install -m 0644 ${WORKDIR}/ethernet.network ${D}${sysconfdir}/systemd/network/
	      sed -i -e "s:@@interface@@:${NAS_IFACE}:" ${D}${sysconfdir}/systemd/network/ethernet.network
}

FILES_${PN} = " \
	       ${sysconfdir}/* \
"
```

6. Ajout de librairies dans le projet
Lorque des librairies externes sont utilisées dans le projet, on peut dire à BitBake de les introduire dans la toolchain avec : 
```bash
$ bitbake *nom_de_l_image* -c populate_sdk
``` 
