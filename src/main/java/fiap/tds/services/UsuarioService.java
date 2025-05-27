package fiap.tds.services;

import fiap.tds.dtos.SearchListDto;
import fiap.tds.dtos.usuarioDto.CreateUsuarioDto;
import fiap.tds.dtos.usuarioDto.ResponseUsuarioDto;
import fiap.tds.entities.Usuario;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.infrastructure.DatabaseConfig;
import fiap.tds.repositories.AutenticaUsuarioRepository;
import fiap.tds.repositories.UsuarioRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class UsuarioService {


    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final AutenticaUsuarioRepository autenticaUsuarioRepository = new AutenticaUsuarioRepository();

    // Registrar novo usuário
    public void registrar(CreateUsuarioDto dto) {

        // Verificando se o objeto possui todos os campos necessários
        if (dto.nomeUsuario() == null || dto.telefoneContato() == null || dto.autenticaUsuario() == null) {
            throw new BadRequestException("Todos os campos obrigatórios devem ser preenchidos.");
        }

        // Verificando se a senha tem no mínimo 8 caracteres
        if (dto.autenticaUsuario().getSenhaUsuario().length() < 8 || dto.autenticaUsuario().getSenhaUsuario().length() > 30) {
            throw new BadRequestException("A senha deve ter no mínimo 8 e no máximo 30 caracteres");
        }

        // Verificando se o email já está em uso
        if (autenticaUsuarioRepository.validarEmailExistente(dto.autenticaUsuario().getEmailUsuario())) {
            throw new BadRequestException("O email informado já está cadastrado");
        }

        // Valiação do tipo
        if (!dto.tipoUsuario().equalsIgnoreCase("CLIENTE") && !dto.tipoUsuario().equalsIgnoreCase("COLABORADOR")) {
            throw new BadRequestException("O tipo de usuário deve ser CLIENTE ou COLABORADOR");
        }

        // Adicionar validação de telefone

        // Convertendo DTO para objeto
        var usuario = new Usuario();
        usuario.setNomeUsuario(dto.nomeUsuario());
        usuario.setTipoUsuario(dto.tipoUsuario());
        usuario.setTelefoneContato(dto.telefoneContato());
        usuario.setIdCidade(dto.idCidade());
        usuario.setAutenticaUsuario(dto.autenticaUsuario());
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDeleted(false);

        usuarioRepository.registrar(usuario);
    }

    // Função para buscar os usuários com lógica de filtros
    public SearchListDto<ResponseUsuarioDto> buscar(int page, String nome, String tipo, String direction) {
        if (page < 1) {
            throw new BadRequestException("O número da página deve ser maior ou igual a 1.");
        }
        final int PAGE_SIZE = 9;
        var resultado = usuarioRepository.buscar(nome, tipo, direction);
        // Informações de paginação
        int totalItems = resultado.totalItems();
        List<Usuario> usuarios = resultado.data();
        int start = Math.max((page - 1) * PAGE_SIZE, 0);
        int end = Math.min(start + PAGE_SIZE, totalItems);
        // Criando a lista que será retornada
        List<ResponseUsuarioDto> pageData = List.of();
        if (start < totalItems) {
            pageData = usuarios.subList(start, end).stream()
                    .map(this::converterUsuarioParaDto)
                    .toList();
        }
        return new SearchListDto<>(page, direction, PAGE_SIZE, totalItems, pageData);
    }

    // Função para buscar colaborador por ID
    public ResponseUsuarioDto buscarPorId(int id) {
        // Validação da existência do colaborador
        var usuarioExistente = usuarioRepository.buscarPorId(id);
        if (usuarioExistente == null) {
            throw new NotFoundException("Colaborador com o ID " + id + " não encontrado");
        }
        return converterUsuarioParaDto(usuarioExistente);
    }

    // Atualizar colaborador (nome e tipo)
    public void atualizar(int id, CreateUsuarioDto dto) {
        var usuarioExistente = usuarioRepository.buscarPorId(id);
        // Validação da existência do colaborador que será atualizado
        if (usuarioExistente == null) {
            throw new NotFoundException("Nenhum colaborador encontrado");
        }
        // Validação dos campos
        if (dto.nomeUsuario() == null || dto.telefoneContato() == null) {
            throw new BadRequestException("Todos os campos obrigatórios devem ser preenchidos.");
        }

        // Adicionar validação de CIDADE


        // Convertendo o DTO em entidade
        var usuario = new Usuario();
        usuario.setDeleted(false);
        usuario.setNomeUsuario(dto.nomeUsuario());
        usuario.setTelefoneContato(dto.telefoneContato());
        usuario.setIdCidade(dto.idCidade());
        usuario.setDataCriacao(LocalDateTime.now());

        usuarioRepository.atualizar(id, usuario);
    }

    // Função para deletar colaboradores
    public void deletar(int id) {
        Usuario usuarioExistente = usuarioRepository.buscarPorId(id);

        if (usuarioExistente == null) {
            throw new NotFoundException("Nenhum usuário encontrado");
        }
        usuarioRepository.deletar(id);
    }

    // Função que converte Usuario em ResponseUsuarioDto
    private ResponseUsuarioDto converterUsuarioParaDto(Usuario a) {
        return new ResponseUsuarioDto(
                a.getIdUsuario(),
                a.isDeleted(),
                a.getDataCriacao(),
                a.getNomeUsuario(),
                a.getTipoUsuario(),
                a.getIdCidade(),
                a.getTelefoneContato()
        );
    }
}
