package com.example;

import com.example.model.joined.Developpeur;
import com.example.model.joined.Employe;
import com.example.model.joined.Manager;
import com.example.model.singletable.Moto;
import com.example.model.singletable.Vehicule;
import com.example.model.singletable.Voiture;
import com.example.model.tableperclass.Electronique;
import com.example.model.tableperclass.Livre;
import com.example.model.tableperclass.Produit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class App {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-inheritance");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // SINGLE_TABLE : une seule table pour tous les véhicules
            em.persist(new Voiture("Renault", 5));
            em.persist(new Voiture("Peugeot", 3));
            em.persist(new Moto("Yamaha", false));
            em.persist(new Moto("Ural", true));

            // JOINED : une table parent + une table par sous-classe
            em.persist(new Developpeur("Sara", "Python"));
            em.persist(new Developpeur("Youssef", "Java"));
            em.persist(new Manager("Karim", 4));
            em.persist(new Manager("Nadia", 2));

            // TABLE_PER_CLASS : une table par classe concrète
            em.persist(new Livre("Effective Java", "Joshua Bloch"));
            em.persist(new Livre("Le Petit Prince", "Antoine de Saint-Exupéry"));
            em.persist(new Electronique("Ordinateur portable", 36));
            em.persist(new Electronique("Casque audio", 12));

            em.getTransaction().commit();

            // Lecture : SINGLE_TABLE
            System.out.println("===== SINGLE_TABLE : Véhicules =====");
            for (Vehicule v : em.createQuery("SELECT v FROM Vehicule v", Vehicule.class).getResultList()) {
                String detail = "";
                if (v instanceof Voiture) {
                    detail = ((Voiture) v).getNombrePortes() + " portes";
                } else if (v instanceof Moto) {
                    detail = "sidecar : " + (((Moto) v).isSidecar() ? "oui" : "non");
                }
                System.out.println(v.getClass().getSimpleName() + " : " + v.getMarque() + " (" + detail + ")");
            }

            // Lecture : JOINED
            System.out.println("\n===== JOINED : Employés =====");
            for (Employe e : em.createQuery("SELECT e FROM Employe e", Employe.class).getResultList()) {
                String detail = "";
                if (e instanceof Developpeur) {
                    detail = "langage : " + ((Developpeur) e).getLangage();
                } else if (e instanceof Manager) {
                    detail = ((Manager) e).getNombreEquipes() + " équipes";
                }
                System.out.println(e.getClass().getSimpleName() + " : " + e.getNom() + " (" + detail + ")");
            }

            // Lecture : TABLE_PER_CLASS
            System.out.println("\n===== TABLE_PER_CLASS : Produits =====");
            for (Produit p : em.createQuery("SELECT p FROM Produit p", Produit.class).getResultList()) {
                String detail = "";
                if (p instanceof Livre) {
                    detail = "auteur : " + ((Livre) p).getAuteur();
                } else if (p instanceof Electronique) {
                    detail = "garantie : " + ((Electronique) p).getGarantie() + " mois";
                }
                System.out.println(p.getClass().getSimpleName() + " : " + p.getNom() + " (" + detail + ")");
            }

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}