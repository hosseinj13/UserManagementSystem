package com.example.usermanagementsystem.connection;

import com.example.usermanagementsystem.model.Admin;
import com.example.usermanagementsystem.model.User;
import com.example.usermanagementsystem.model.VerificationToken;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class SessionFactorySingleton {
    private SessionFactorySingleton() {
    }

    private static class LazyHolder {
        static SessionFactory INSTANCE;

        static {
            var registry = new StandardServiceRegistryBuilder()
                    .configure ()
                    .build ();
            INSTANCE = new MetadataSources( registry )
                    .addAnnotatedClass(VerificationToken.class)
                    .addAnnotatedClass ( User.class )
                    .addAnnotatedClass(Admin.class)
                    .buildMetadata ()
                    .buildSessionFactory ();
        }
    }

    public static SessionFactory getInstance() {
        return LazyHolder.INSTANCE;
    }
}