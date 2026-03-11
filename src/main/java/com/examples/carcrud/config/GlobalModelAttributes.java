package com.examples.carcrud.config;

import com.examples.carcrud.service.AppUserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

/**
 * @ControllerAdvice — Cette classe s'applique a TOUS les controleurs automatiquement.
 * Elle permet de factoriser du code commun au lieu de le repeter dans chaque controleur.
 *
 * Ici, on ajoute l'utilisateur connecte ("currentUser") au Model avant chaque requete,
 * pour que tous les templates Thymeleaf puissent afficher son nom sans que chaque
 * controleur ait besoin de le faire manuellement.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    private final AppUserService appUserService;

    public GlobalModelAttributes(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     * @ModelAttribute — Cette methode est executee AVANT chaque methode de controleur
     * (@GetMapping, @PostMapping, etc.) dans toute l'application.
     *
     * @param model     le "sac de donnees" envoye au template Thymeleaf
     * @param principal l'utilisateur connecte (fourni par Spring Security, null si non connecte)
     *
     * Resultat : l'attribut "currentUser" est disponible dans tous les templates
     * via ${currentUser.firstName}, ${currentUser.role}, etc.
     */
    @ModelAttribute
    public void addUserInfo(Model model, Principal principal) {
        // Si un utilisateur est connecte (principal != null)
        if (principal != null) {
            // Recuperer l'entite AppUser depuis la base via son username
            var user = appUserService.findByUsername(principal.getName());
            if (user != null) {
                // Ajouter l'utilisateur au Model pour que Thymeleaf puisse y acceder
                model.addAttribute("currentUser", user);
            }
        }
    }
}
