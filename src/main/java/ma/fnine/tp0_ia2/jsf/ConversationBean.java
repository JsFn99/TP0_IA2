package ma.fnine.tp0_ia2.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Named
@ViewScoped
public class ConversationBean implements Serializable {

    
    private String systemRole = "helpful assistant";

    
    private boolean systemRoleChangeable = true;

    
    private String question;

    
    private String reponse;

    
    private StringBuilder conversation = new StringBuilder();

    @Inject
    private FacesContext facesContext;

    public ConversationBean() {
    }

    // Getters et setters
    public String getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
    }

    public boolean isSystemRoleChangeable() {
        return systemRoleChangeable;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getConversation() {
        return conversation.toString();
    }

    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }

    /**
     * Envoie la question au serveur.
     * Traitement : met en évidence les mots longs (plus de 6 caractères) en les entourant d'astérisques.
     *
     * @return null pour rester sur la même page.
     */
    public String envoyer() {
        if (question == null || question.isBlank()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Texte question vide", "Il manque le texte de la question");
            facesContext.addMessage(null, message);
            return null;
        }

        // Initialisation de la réponse
        this.reponse = "";
        this.systemRoleChangeable = false;

        // Traitement : trouver les mots longs (plus de 6 caractères)
        String[] tokens = question.split(" ");
        List<String> motsLongs = new ArrayList<>();

        for (String token : tokens) {
            if (token.length() > 6) {
                motsLongs.add("***" + token.toUpperCase(Locale.FRENCH) + "***");
            }
        }

        // Ajouter le rôle de l'API en début de réponse
        this.reponse += "Rôle : " + systemRole.toUpperCase(Locale.FRENCH) + "\n";

        // Ajouter les mots longs
        if (!motsLongs.isEmpty()) {
            this.reponse += "Mots longs détectés :\n" + String.join(" ", motsLongs) + "\n";
            this.reponse += "Total : " + motsLongs.size() + " mot(s) long(s).\n";
        } else {
            this.reponse += "Aucun mot long détecté dans votre question.\n";
        }

        // Afficher la conversation dans l'historique
        afficherConversation();
        return null;
    }

    /**
     * Pour un nouveau chat.
     * @return "index" pour recommencer une nouvelle conversation.
     */
    public String nouveauChat() {
        return "index";
    }

    /**
     * Pour afficher la conversation dans le textArea de la page JSF.
     */
    private void afficherConversation() {
        this.conversation.append("* Utilisateur:\n").append(question)
                .append("\n* Serveur:\n").append(reponse).append("\n");
    }

    /**
     * Retourne la liste des rôles systèmes disponibles.
     * @return liste des rôles systèmes.
     */
    public List<SelectItem> getSystemRoles() {
        List<SelectItem> listeSystemRoles = new ArrayList<>();
        String role = """
                You are an interpreter. You translate from English to French and from French to English.
                If the user type a French text, you translate it into English.
                If the user type an English text, you translate it into French.
                If the text contains only one to three words, give some examples of usage of these words in English.
                """;
        listeSystemRoles.add(new SelectItem(role, "Traducteur Anglais-Français"));
        role = """
                You are a travel guide. If the user types the name of a country or town,
                you tell them the main places to visit and the average price of a meal.
                """;
        listeSystemRoles.add(new SelectItem(role, "Guide touristique"));
        return listeSystemRoles;
    }
}
