package com.forumhub.controller;

import com.forumhub.dto.LoginRequest;
import com.forumhub.dto.RegisterRequest;
import com.forumhub.dto.RegisterResponse;
import com.forumhub.dto.TokenResponse;
import com.forumhub.model.User;
import com.forumhub.repository.UserRepository;
import com.forumhub.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Login - valida usuário existente e retorna token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        // Cria token de autenticação com login e senha
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.login(), request.senha());

        // Spring Security valida automaticamente comparando com hash do banco
        Authentication authentication = authenticationManager.authenticate(authToken);

        // Gera token JWT para o usuário autenticado
        User user = (User) authentication.getPrincipal();
        String token = tokenService.generateToken(user);

        return ResponseEntity.ok(new TokenResponse(token));
    }

    /**
     * Register - cadastra novo usuário com senha hasheada
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        // Verificar se login já existe
        if (userRepository.existsByLogin(request.login())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Login já está em uso");
        }

        // Criar novo usuário
        User newUser = new User();
        newUser.setLogin(request.login());
        newUser.setNome(request.nome());
        newUser.setEmail(request.email());

        // AQUI: Hash da senha é gerado automaticamente
        String hashedPassword = passwordEncoder.encode(request.senha());
        newUser.setSenha(hashedPassword);

        // Salvar no banco de dados
        User savedUser = userRepository.save(newUser);

        // Retornar resposta
        RegisterResponse response = new RegisterResponse(
                savedUser.getId(),
                savedUser.getLogin(),
                savedUser.getNome(),
                savedUser.getEmail(),
                "Usuário cadastrado com sucesso!"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}