package fiap.tds.services;

import fiap.tds.dtos.SearchListDto;
import fiap.tds.dtos.colaborador.ColaboradorCreateDto;
import fiap.tds.dtos.colaborador.ColaboradorResponseDto;
import fiap.tds.entities.Colaborador;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.repositories.AutenticaColaboradorRepository;
import fiap.tds.repositories.ColaboradorRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ColaboradorService {


    private final ColaboradorRepository colaboradorRepository = new ColaboradorRepository();
    private final AutenticaColaboradorRepository autenticaColaboradorRepository = new AutenticaColaboradorRepository();

    // Registrar novo colaborador
        public void registrar(ColaboradorCreateDto dto) {

        // Verificando se o objeto possui todos os campos necessários
        if (dto.nomeColaborador() == null || dto.tipoColaborador() == null || dto.autenticaColaborador() == null) {
            throw new BadRequestException("Todos os campos obrigatórios (nome, tipo, autentica) devem ser preenchidos.");
        }

        // Verificando se a senha tem no mínimo 8 caracteres
        if (dto.autenticaColaborador().getSenha().length() < 8 || dto.autenticaColaborador().getSenha().length() > 30) {
            throw new BadRequestException("A senha deve ter no mínimo 8 e no máximo 30 caracteres");
        }

        // Verificando se o email já está em uso
        if (autenticaColaboradorRepository.validarEmailExistente(dto.autenticaColaborador().getEmail())) {
            throw new BadRequestException("O email informado já está cadastrado");
        }

        // Valiação do tipo
        if (!dto.tipoColaborador().equalsIgnoreCase("OPERADOR") && !dto.tipoColaborador().equalsIgnoreCase("ADMIN")) {
            throw new BadRequestException("O tipo de colaborador deve ser OPERADOR ou ADMIN");
        }

        // Convertendo DTO para objeto
        var colaborador = new Colaborador();
        colaborador.setNomeColaborador(dto.nomeColaborador());
        colaborador.setTipoColaborador(dto.tipoColaborador());
        colaborador.setAutenticaColaborador(dto.autenticaColaborador());
        colaborador.setDataCriacao(LocalDateTime.now());
        colaborador.setDeleted(false);

        colaboradorRepository.registrar(colaborador);
    }

    // Função para buscar os colaboradores com lógica de validação
    public SearchListDto<ColaboradorResponseDto> buscar(int page, String nome, String tipo, String direction) {
        if (page < 1) {
            throw new BadRequestException("O número da página deve ser maior ou igual a 1.");
        }
        final int PAGE_SIZE = 9;
        var resultado = colaboradorRepository.buscar(nome, tipo, direction);
        // Informações de paginação
        int totalItems = resultado.totalItems();
        List<Colaborador> colaboradores = resultado.data();
        int start = Math.max((page - 1) * PAGE_SIZE, 0);
        int end = Math.min(start + PAGE_SIZE, totalItems);
        // Criando a lista que será retornada
        List<ColaboradorResponseDto> pageData = List.of();
        if (start < totalItems) {
            pageData = colaboradores.subList(start, end).stream()
                    .map(this::converterColaboradorParaDto)
                    .toList();
        }
        return new SearchListDto<>(page, direction, PAGE_SIZE, totalItems, pageData);
        }

    // Função para buscar colaborador por ID
    public ColaboradorResponseDto buscarPorId(int id) {
        // Validação da existência do colaborador
        var colaboradorExistente = colaboradorRepository.buscarPorId(id);
        if (colaboradorExistente == null) {
            throw new NotFoundException("Colaborador com o ID " + id + " não encontrado");
        }
        return converterColaboradorParaDto(colaboradorExistente);
    }

    // Atualizar colaborador (nome e tipo)
    public void atualizar(int id, ColaboradorCreateDto dto) {
        var colaboradorExistente = colaboradorRepository.buscarPorId(id);
        // Validação da existência do colaborador que será atualizado
        if (colaboradorExistente == null) {
            throw new NotFoundException("Nenhum colaborador encontrado");
        }
        // Validação dos campos
        if (dto.nomeColaborador() == null || dto.tipoColaborador() == null) {
            throw new BadRequestException("Todos os campos obrigatórios (nome, tipo) devem ser preenchidos.");
        }
        // Validação do tipo
        if (colaboradorExistente.getTipoColaborador().equalsIgnoreCase("OPERADOR")
                && colaboradorExistente.getTipoColaborador().equalsIgnoreCase("ADMIN")) {
            throw new BadRequestException("O tipo de colaborador deve ser OPERADOR ou ADMIN");
        }
        // Convertendo o DTO em entidade
        var colaborador = new Colaborador();
        colaborador.setDeleted(false);
        colaborador.setNomeColaborador(dto.nomeColaborador());
        colaborador.setTipoColaborador(dto.tipoColaborador());
        colaborador.setDataCriacao(LocalDateTime.now());

        colaboradorRepository.atualizar(id, colaborador);
    }

    // Função para deletar colaboradores
    public void deletar(int id) {
        Colaborador colaboradorExistente = colaboradorRepository.buscarPorId(id);

        if (colaboradorExistente == null) {
            throw new NotFoundException("Nenhum colaborador encontrado");
        }
        colaboradorRepository.deletar(id);
    }

    // Função que converte Colaborador em ColaboradorResponseDto
    private ColaboradorResponseDto converterColaboradorParaDto(Colaborador a) {
        return new ColaboradorResponseDto(
                a.getId(),
                a.isDeleted(),
                a.getDataCriacao(),
                a.getNomeColaborador(),
                a.getTipoColaborador()
        );
    }
}
