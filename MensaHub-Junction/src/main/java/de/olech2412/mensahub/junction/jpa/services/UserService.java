package de.olech2412.mensahub.junction.jpa.services;

import de.olech2412.mensahub.junction.jpa.repository.ErrorEntityRepository;
import de.olech2412.mensahub.junction.jpa.repository.UsersRepository;
import de.olech2412.mensahub.models.authentification.Role;
import de.olech2412.mensahub.models.authentification.Users;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.Application;
import de.olech2412.mensahub.models.result.errors.ErrorEntity;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import de.olech2412.mensahub.models.result.errors.jpa.JPAErrors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    ErrorEntityRepository errorEntityRepository;

    public Result<List<Users>, JPAError> findAll() {
        try {
            List<Users> users = usersRepository.findAll();
            return Result.success(users);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<List<Users>, JPAError> result = Result.error(new JPAError("Fehler beim abrufen aller User: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<List<Users>, JPAError> findAllByEnabled(boolean enabled) {
        try {
            List<Users> users = usersRepository.findUsersByEnabled(enabled);
            return Result.success(users);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<List<Users>, JPAError> result = Result.error(new JPAError("Fehler beim abrufen von Usern nach freischaltung: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<List<Users>, JPAError> findAllByRole(Role role) {
        try {
            List<Users> users = usersRepository.findAllByRole(role);
            return Result.success(users);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<List<Users>, JPAError> result = Result.error(new JPAError("Fehler beim abrufen von Usern mit Rolle: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<List<Users>, JPAError> findAllBy2Roles(Role role1, Role role2) {
        try {
            List<Users> users = usersRepository.findAllByRoleOrRole(role1, role2);
            return Result.success(users);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<List<Users>, JPAError> result = Result.error(new JPAError("Fehler beim abrufen von Usern mit Rolle: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<Users, JPAError> deleteUser(Users users) {
        try {
            usersRepository.delete(users);
            return Result.success(users);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<Users, JPAError> result = Result.error(new JPAError("Fehler beim löschen des Users: " + e.getMessage(), JPAErrors.ERROR_DELETE));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<Users, JPAError> findByUsername(String username) {
        try {
            Users users = usersRepository.findByUsername(username);
            return Result.success(users);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<Users, JPAError> result = Result.error(new JPAError("Fehler beim abrufen von Usern mit Nutzerkennung: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<Users, JPAError> saveUser(Users users) {
        try {
            usersRepository.save(users);
            return Result.success(users);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<Users, JPAError> result = Result.error(new JPAError("Fehler beim speichern des Users: " + e.getMessage(), JPAErrors.ERROR_SAVING));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

}
