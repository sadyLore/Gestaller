# Gestaller: Sistema de Gestión para Talleres Mecánicos

**Gestaller** es una aplicación móvil para Android diseñada para la gestión integral de talleres mecánicos.  
Permite administrar clientes, vehículos, plantillas de servicios y órdenes de trabajo, incorporando funcionalidades modernas como autenticación biométrica y captura de evidencias fotográficas.

Esta aplicación fue desarrollada como **proyecto académico de la Universidad Paraguayo Alemana**, por las estudiantes de Ingeniería en Tecnología Empresarial del segundo año (año vigente 2025), aplicando buenas prácticas de ingeniería de software y una arquitectura limpia basada en **MVVM** y componentes **Jetpack** de Android.

---

## Descripción general

El propósito de Gestaller es brindar una herramienta que facilite las tareas administrativas en un taller mecánico.  
Mediante una interfaz sencilla, el usuario puede:

- Registrar y mantener información de clientes y sus vehículos.  
- Crear y reutilizar plantillas de servicios para agilizar la generación de órdenes.  
- Generar, consultar y actualizar órdenes de trabajo asociadas a clientes y vehículos.  
- Adjuntar fotografías como evidencia del estado de un vehículo o del avance de un servicio.  
- Autenticarse mediante usuario/contraseña y huella digital (en dispositivos compatibles).  
- Elegir entre tema claro y oscuro para la interfaz.  

La persistencia de datos se implementa mediante **Room**, lo que garantiza integridad y acceso eficiente a la base de datos local.  
La sincronización y almacenamiento de fotografías se realiza a través de **Firebase**.

---

## Funcionalidades principales

### Gestión de clientes
- Alta, edición, búsqueda y eliminación de clientes.  
- Campos: nombre (obligatorio), teléfono y dirección.  
- Formulario con validación de campos y control de tema claro/oscuro.

### Gestión de vehículos
- Registro de vehículos (marca, modelo, etc.).  
- Asociación de vehículos a clientes existentes.  
- Edición y eliminación de registros.

### Gestión de servicios
- Creación de plantillas para trabajos frecuentes.  
- Campos: nombre, descripción detallada y precio por defecto.  
- Reutilización de plantillas al generar órdenes.

### Gestión de órdenes de trabajo
- Creación de órdenes que vinculan cliente, vehículo y servicios.  
- Cálculo automático de costos.  
- Adjuntar fotografías con la cámara del dispositivo.  
- Visualización de listas y detalles con estado actualizado.

### Autenticación y seguridad
- Inicio de sesión local o con Firebase Authentication.  
- Autenticación biométrica (huella digital).  
- Cierre de sesión desde la interfaz.

### Experiencia de usuario y diseño
- Navegación intuitiva basada en Activities y Fragments.  
- Listas con RecyclerView y adaptadores personalizados.  
- Temas claro y oscuro configurables.  
- Formularios consistentes en todas las pantallas.  
- Pantalla principal con trabajos recientes y búsqueda de órdenes.

---

## Arquitectura y tecnologías

La aplicación sigue una arquitectura **MVVM (Model–View–ViewModel)** para desacoplar la presentación, la lógica de negocio y los datos.

**Lenguaje:** Java (Android SDK 21+)

**Android Jetpack:**
- Room (entidades, DAOs, repositorios).  
- LiveData y ViewModel.  
- Navigation Components.  

**Firebase:**
- Firebase Authentication.  
- Cloud Firestore y Firebase Storage.  

**Otros:**
- CameraX para captura de imágenes.  
- Material Design para interfaz.

---

## Requisitos

- Android Studio 2023.2.1 o posterior.  
- JDK 1.8.  
- Archivo `google-services.json` configurado.  
- Android 5.0 (API 21) o superior.  
- Hardware de huella digital (opcional).

---

## Instalación y ejecución

```bash
git clone https://github.com/sadyLore/Gestaller.git
cd Gestaller

com.example.gestaller.data      → Entidades, DAOs y repositorios  
com.example.gestaller.ui         → Activities, Fragments y adaptadores  
com.example.gestaller.viewmodel  → ViewModels  
com.tallermanager                → Componentes auxiliares  

