# :house: :mask: Quarantine Tracker :round_pushpin:

![alt text](./images/total.png)
## A Propos
Application de quaratine tracker utilisant Kotlin pour Android. Ce projet porte sur la création d'une application tracker Android à l'aide de Kotlin. Pour obtenir la position actuelle, nous avons utilisé l'API Google Cloud. Des informations telles que le nom d'utilisateur, l'adresse, e-mail etc... sont transmises à partir de Firebase les cordonnéesque nous avons enregistré sur la page d'inscription.

Tout d'abord, nous devez nous inscrire avant de nous connecter à l'application. Ensuite, dans notre compte personnel, nos informations personnelles, notre emplacement et la distance entre notre adresse de quarantine et notre adresse actuelle s'affichent. Si nous êtes en hors de la distance recommandée, nous recevons une notification sur notre téléphone.

<img src="https://blog.lesjeudis.com/wp-content/uploads/2020/10/KOTLING-LOGO-1.png"  width="250" height="150" />  <img src="https://firebase.google.com/downloads/brand-guidelines/PNG/logo-built_white.png" width="250" height="150" />   <img src="https://ubunlog.com/wp-content/uploads/2019/08/Android.png" width="300" height="150" /> 




## Exigences

* Android studio last version
* JDK 11
* Android SDK 31
* Supports API Level +21
* Material Components 1.5.0-alpha04
* Vous devez configurer Firebase dans votre propre compte
* Taille de la machine virtuelle : 1440 x 3040 ou 1080 x 2160

	
## Dépendances externes

Voici les API/services externes utilisés pour cette application :
* Google Cloud `(https://console.cloud.google.com/)`L'API principale utilisée pour les données de localisation actuelles.
* Firebase `( https://firebase.google.com/ )` Un service de session privée est fourni aux utilisateurs.
