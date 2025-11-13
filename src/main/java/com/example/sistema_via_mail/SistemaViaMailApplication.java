package com.example.sistema_via_mail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicación principal del Sistema Trans Comarapa
 * Sistema de gestión vía correo electrónico
 */
@SpringBootApplication
@EnableScheduling  // HABILITADO - usando POP3 puerto 110 sin SSL
public class SistemaViaMailApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaViaMailApplication.class, args);
		System.out.println("=================================================");
		System.out.println("SISTEMA TRANS COMARAPA - Iniciado correctamente");
		System.out.println("=================================================");
		System.out.println("Correo monitoreado: grupo04sa@tecnoweb.org.bo");
		System.out.println("Protocolo: POP3 (Puerto 110 - Sin SSL)");
		System.out.println("Base de datos: db_grupo04sa");
		System.out.println("Fase de implementación: FASE 1 - Configuración y Base");
		System.out.println("=================================================");
	}

}
