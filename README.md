
#  Système de Gestion de Produits 📂

![Langage](https://img.shields.io/badge/Langage-Java-orange.svg)
![Framework](https://img.shields.io/badge/Framework-Spring%20Boot-green.svg)
![Sécurité](https://img.shields.io/badge/Sécurité-Spring%20Security-blue.svg)
![Build](https://img.shields.io/badge/Build-Maven-C71A36.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

Une application web moderne et simple pour la gestion de produits. Elle permet aux utilisateurs de consulter, rechercher, ajouter et supprimer des produits via une interface intuitive et sécurisée.

---

### ✨ Aperçu en Images

<details>
<summary>Cliquez ici pour voir les captures d'écran de l'application</summary>

**Page de Connexion**
*L'accès à l'application est protégé par Spring Security.*
![Page de Connexion](auth.png)

**Liste des Produits (Vue Utilisateur)**
*Vue paginée et consultable par tous les utilisateurs connectés.*
![Liste des Produits](liste_user.png)

**Liste des Produits (Vue Admin)**
*Le rôle "ADMIN" peut voir et utiliser le bouton de suppression.*
![Liste des Produits avec suppression](list_products_admin.png)

**Formulaire d'Ajout**
*Formulaire simple pour créer un nouveau produit (accès admin).*
![Formulaire d'Ajout de Produit](add_product.png)

**Validation des Données**
*La validation côté serveur empêche la soumission de données incorrectes.*
![Formulaire d'Ajout de Produit avec Validation](add_product1.png)

</details>

---

## 🚀 Fonctionnalités

*   🔐 **Accès Sécurisé** : Système de connexion avec deux rôles (`USER` et `ADMIN`).
*   📋 **Liste de Produits** : Affichage des produits dans une vue paginée pour une navigation facile.
*   🔍 **Recherche Avancée** : Filtrez les produits par nom pour trouver rapidement ce que vous cherchez.
*   ➕ **Ajout de Produit** : Les administrateurs peuvent ajouter de nouveaux produits via un formulaire dédié.
*   ✅ **Validation Intégrée** : Contrôles de validation sur les formulaires pour assurer l'intégrité des données.
*   🗑️ **Suppression de Produit** : Fonctionnalité de suppression sécurisée, réservée aux administrateurs.

## 🛠️ Technologies Utilisées

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![H2](https://img.shields.io/badge/H2-484848?style=for-the-badge&logo=h2&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

---

### 🔌 Extraits de Code Clés

<details>
<summary><code>Product.java</code> (Entité)</summary>

```java
package com.example.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Size(min = 2,max = 50)
    private String name;
    @Min(100)
    private double price;
    private int quantity;
}
```
</details>

<details>
<summary><code>ProductRepository.java</code> (Repository)</summary>

```java
package com.example.demo.repositories;

import com.example.demo.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findByNameContains(String keyword, Pageable pageable);
}
```
</details>

<details>
<summary><code>ProductController.java</code> (Contrôleur)</summary>

```java
package com.example.demo.web;

import com.example.demo.entities.Product;
import com.example.demo.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class ProductController {
    private ProductRepository productRepository;

    @GetMapping("/")
    public String home(){
        return "redirect:/user/index";
    }
    @GetMapping("/user/index")
    public String index(Model model,
                        @RequestParam(name = "page",defaultValue = "0") int page,
                        @RequestParam(name = "size",defaultValue = "5") int size,
                        @RequestParam(name = "keyword",defaultValue = "") String kw
    ){
        Page<Product> pageProducts = productRepository.findByNameContains(kw, PageRequest.of(page,size));
        model.addAttribute("listProducts",pageProducts.getContent());
        model.addAttribute("pages",new int[pageProducts.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("keyword",kw);
        return "products";
    }
    @GetMapping("/admin/deleteProduct")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteProduct(@RequestParam(name = "id") Long id,
                                @RequestParam(name = "keyword",defaultValue = "") String keyword,
                                @RequestParam(name = "page",defaultValue = "0") int page
                                ){
        productRepository.deleteById(id);
        return "redirect:/user/index?page="+page+"&keyword="+keyword;
    }
    @GetMapping("/admin/newProduct")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String newProduct(Model model){
        model.addAttribute("product",new Product());
        return "new-product";
    }
    @PostMapping("/admin/saveProduct")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String saveProduct(@Valid Product product, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "new-product";
        }
        productRepository.save(product);
        return "redirect:/user/index";
    }
}
```
</details>

---

## ⚙️ Configuration et Installation

Pour lancer ce projet localement, suivez ces étapes :

1.  **Clonez le dépôt :**
    ```bash
    git clone https://github.com/adamfutur/productsManagementSystem-main.git
    ```

2.  **Naviguez vers le répertoire du projet :**
    ```bash
    cd productsManagementSystem-main
    ```

3.  **Exécutez l'application avec Maven :**
    ```bash
    mvn spring-boot:run
    ```

4.  **Accédez à l'application :**
    Ouvrez votre navigateur et allez à l'adresse `http://localhost:8080`

### 🔑 Identifiants de Connexion

> Vous pouvez utiliser les comptes suivants pour tester les différents rôles :
>
> *   **Rôle :** `USER`
>     *   **Utilisateur :** `user`
>     *   **Mot de passe :** `1234`
>
> *   **Rôle :** `ADMIN`
>     *   **Utilisateur :** `admin`
>     *   **Mot de passe :** `1234`
