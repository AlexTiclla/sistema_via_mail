package com.example.sistema_via_mail.repository;

import com.example.sistema_via_mail.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario
 * Gestiona propietarios, secretarias y conductores (CU1)
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Buscar usuario por CI
     * @param ci Cédula de identidad
     * @return Optional con el usuario encontrado
     */
    Optional<Usuario> findByCi(String ci);

    /**
     * Buscar usuarios por tipo
     * @param tipoUsuario Tipo de usuario (PROPIETARIO, SECRETARIA, CONDUCTOR)
     * @return Lista de usuarios del tipo especificado
     */
    List<Usuario> findByTipoUsuario(String tipoUsuario);

    /**
     * Buscar usuarios por estado
     * @param estado Estado del usuario (ACTIVO, INACTIVO)
     * @return Lista de usuarios con el estado especificado
     */
    List<Usuario> findByEstado(String estado);

    /**
     * Buscar usuarios por tipo y estado
     * @param tipoUsuario Tipo de usuario
     * @param estado Estado del usuario
     * @return Lista de usuarios que cumplen ambos criterios
     */
    List<Usuario> findByTipoUsuarioAndEstado(String tipoUsuario, String estado);

    /**
     * Verificar si existe un usuario con el CI dado
     * @param ci Cédula de identidad
     * @return true si existe, false en caso contrario
     */
    boolean existsByCi(String ci);

    /**
     * Buscar usuario por email
     * @param email Email del usuario
     * @return Optional con el usuario encontrado
     */
    Optional<Usuario> findByEmail(String email);
}

