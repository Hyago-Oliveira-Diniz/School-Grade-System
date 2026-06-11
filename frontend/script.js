const API = "http://localhost:8080/api";

// =========================
// HELPERS
// =========================
function getToken() {
    return localStorage.getItem("token");
}

function getUsuario() {
    const u = localStorage.getItem("usuarioLogado");
    return u ? JSON.parse(u) : null;
}

function authHeaders() {
    return {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + getToken()
    };
}

// =========================
// LOGIN
// =========================
function login() {
    const usuario = document.getElementById("loginUsuario").value;
    const senha   = document.getElementById("loginSenha").value;

    if (!usuario || !senha) {
        alert("Preencha todos os campos!");
        return;
    }

    fetch(API + "/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ usuario, senha })
    })
    .then(async res => {
        if (!res.ok) throw new Error("Usuário ou senha inválidos!");
        return res.json();
    })
    .then(data => {
        localStorage.setItem("token", data.token);
        localStorage.setItem("usuarioLogado", JSON.stringify({
            id:      data.id,
            nome:    data.nome,
            usuario: data.usuario,
            tipo:    data.tipo
        }));

        const tipo = data.tipo ? data.tipo.toString().toUpperCase() : "";
        if (tipo === "PROFESSOR")    window.location.href = "professor.html";
        else if (tipo === "ADMIN")   window.location.href = "admin.html";
        else                         window.location.href = "aluno.html";
    })
    .catch(err => alert(err.message));
}

// =========================
// LOGOUT
// =========================
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("usuarioLogado");
    window.location.href = "index.html";
}

// =========================
// MOSTRAR USUÁRIO LOGADO
// =========================
function mostrarUsuario() {
    const usuario = getUsuario();
    const el = document.getElementById("usuarioLogado");
    if (usuario && el) {
        el.innerText = "Bem-vindo, " + usuario.nome;
    }
}

// =========================
// MOSTRAR SEÇÃO
// =========================
function mostrarSecao(secao) {
    const secoes = ["dashboard", "usuarios", "aulas", "professores", "notas", "turmas",
                    "frequencias", "alunos", "disciplinas", "lancarNota", "lancarFrequencia"];
    secoes.forEach(s => {
        const el = document.getElementById(s);
        if (el) el.style.display = "none";
    });

    const alvo = document.getElementById(secao);
    if (alvo) alvo.style.display = "block";

    if (secao === "usuarios")      carregarUsuarios();
    if (secao === "aulas")         carregarAulas();
    if (secao === "professores")   carregarProfessores();
    if (secao === "notas")         carregarNotas();
    if (secao === "turmas")        carregarTurmas();
    if (secao === "frequencias")   carregarFrequencias();
    if (secao === "alunos")        carregarAlunos();
    if (secao === "disciplinas")   carregarDisciplinas();
    if (secao === "lancarNota")    prepararLancarNota();
    if (secao === "lancarFrequencia") prepararLancarFrequencia();
}

// =========================
// CADASTRO — SALVAR USUÁRIO
// =========================
function salvarUsuario(usuario) {
    fetch(API + "/cadastro", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(usuario)
    })
    .then(async res => {
        if (!res.ok) {
            const erro = await res.text();
            throw new Error(erro.includes("já existe") ? "Esse usuário já está cadastrado!" : "Erro ao cadastrar!");
        }
        return res.json();
    })
    .then(() => {
        alert("Cadastro realizado com sucesso!");
        window.location.href = "index.html";
    })
    .catch(err => alert("⚠️ " + err.message));
}

// =========================
// CADASTRO PROFESSOR
// =========================
function cadastrarProfessor() {
    const nome     = document.getElementById("nome").value;
    const usuario  = document.getElementById("usuario").value;
    const senha    = document.getElementById("senha").value;
    const materia  = document.getElementById("materia").value;
    const registro = document.getElementById("registro").value;

    if (!nome || !usuario || !senha || !materia) {
        alert("Preencha todos os campos!");
        return;
    }

    salvarUsuario({ nome, usuario, senha, tipo: "PROFESSOR", materia, registro });
}

// =========================
// CADASTRO ALUNO
// =========================
function cadastrarAluno() {
    const nome      = document.getElementById("nome").value;
    const usuario   = document.getElementById("usuario").value;
    const senha     = document.getElementById("senha").value;
    const matricula = document.getElementById("matricula").value;

    if (!nome || !usuario || !senha) {
        alert("Preencha todos os campos!");
        return;
    }

    salvarUsuario({ nome, usuario, senha, tipo: "ALUNO", matricula });
}

// =========================
// CARREGAR USUÁRIOS (só ADMIN)
// =========================
function carregarUsuarios() {
    fetch(API + "/usuarios", { headers: authHeaders() })
    .then(res => {
        if (res.status === 403) throw new Error("Acesso negado.");
        return res.json();
    })
    .then(usuarios => {
        const lista = document.getElementById("listaUsuarios");
        if (!lista) return;
        lista.innerHTML = "";
        usuarios.forEach(u => {
            const li = document.createElement("li");
            li.innerHTML = `<strong>${u.nome}</strong> — ${u.tipo} (${u.usuario})`;
            lista.appendChild(li);
        });
    })
    .catch(err => console.error("Erro ao carregar usuários:", err.message));
}

// =========================
// CARREGAR TURMAS
// =========================
function carregarTurmas() {
    fetch(API + "/turmas", { headers: authHeaders() })
    .then(res => res.json())
    .then(turmas => {
        const lista = document.getElementById("listaTurmas");
        if (!lista) return;
        lista.innerHTML = "";
        turmas.forEach(t => {
            const li = document.createElement("li");
            li.innerHTML = `<strong>${t.nome}</strong> — ${t.ano} | ${t.periodo}`;
            lista.appendChild(li);
        });
    })
    .catch(err => console.error("Erro ao carregar turmas:", err.message));
}

// =========================
// CARREGAR ALUNOS (ADMIN/PROFESSOR)
// =========================
function carregarAlunos() {
    fetch(API + "/alunos", { headers: authHeaders() })
    .then(res => res.json())
    .then(alunos => {
        const lista = document.getElementById("listaAlunos");
        if (!lista) return;
        lista.innerHTML = "";
        alunos.forEach(a => {
            const li = document.createElement("li");
            li.innerHTML = `<strong>${a.nome}</strong> — Matrícula: ${a.matricula || "—"} (ID: ${a.id})`;
            lista.appendChild(li);
        });
    })
    .catch(err => console.error("Erro ao carregar alunos:", err.message));
}

// =========================
// CARREGAR DISCIPLINAS
// =========================
function carregarDisciplinas() {
    fetch(API + "/disciplinas", { headers: authHeaders() })
    .then(res => res.json())
    .then(disciplinas => {
        const lista = document.getElementById("listaDisciplinas");
        if (!lista) return;
        lista.innerHTML = "";
        disciplinas.forEach(d => {
            const li = document.createElement("li");
            li.innerHTML = `<strong>${d.nome}</strong> — Código: ${d.codigo || "—"}`;
            lista.appendChild(li);
        });
    })
    .catch(err => console.error("Erro ao carregar disciplinas:", err.message));
}

// =========================
// CARREGAR NOTAS DO ALUNO LOGADO
// =========================
function carregarNotas() {
    const usuario = getUsuario();
    if (!usuario) return;

    // Para aluno: busca pelo id salvo no localStorage
    // Para admin/professor: mostra campo de busca
    const tipo = usuario.tipo ? usuario.tipo.toString().toUpperCase() : "";

    if (tipo === "ALUNO") {
        // Busca aluno por usuário para pegar o ID correto
        fetch(API + "/alunos", { headers: authHeaders() })
        .then(res => res.json())
        .then(alunos => {
            const aluno = alunos.find(a => a.usuario && a.usuario.usuario === usuario.usuario);
            if (!aluno) {
                const lista = document.getElementById("listaNotas");
                if (lista) lista.innerHTML = "<li>Nenhuma nota encontrada para este usuário.</li>";
                return;
            }
            return fetch(API + "/notas/aluno/" + aluno.id, { headers: authHeaders() });
        })
        .then(res => res && res.json())
        .then(notas => {
            if (!notas) return;
            renderizarNotas(notas);
        })
        .catch(err => console.error("Erro ao carregar notas:", err.message));
    } else {
        // Admin/Professor: mostrar campo para buscar por aluno
        const lista = document.getElementById("listaNotas");
        if (lista) lista.innerHTML = `
            <div class="form-busca">
                <label>ID do Aluno:</label>
                <input type="number" id="buscarAlunoId" placeholder="Digite o ID do aluno">
                <button onclick="buscarNotasPorAluno()">Buscar</button>
            </div>
        `;
    }
}

function buscarNotasPorAluno() {
    const id = document.getElementById("buscarAlunoId").value;
    if (!id) { alert("Digite o ID do aluno."); return; }
    fetch(API + "/notas/aluno/" + id, { headers: authHeaders() })
    .then(res => res.json())
    .then(notas => renderizarNotas(notas))
    .catch(err => console.error("Erro:", err.message));
}

function renderizarNotas(notas) {
    const lista = document.getElementById("listaNotas");
    if (!lista) return;
    if (!notas || notas.length === 0) {
        lista.innerHTML = "<li>Nenhuma nota encontrada.</li>";
        return;
    }
    lista.innerHTML = "";
    notas.forEach(n => {
        const li = document.createElement("li");
        li.innerHTML = `
            <strong>${n.disciplina?.nome || "—"}</strong>
            | Bimestre ${n.bimestre}: <strong>${n.valor}</strong>
            ${n.notaFinal != null ? "| Média: <strong>" + n.notaFinal + "</strong>" : ""}
        `;
        lista.appendChild(li);
    });
}

// =========================
// CARREGAR FREQUÊNCIAS DO ALUNO LOGADO
// =========================
function carregarFrequencias() {
    const usuario = getUsuario();
    if (!usuario) return;

    const tipo = usuario.tipo ? usuario.tipo.toString().toUpperCase() : "";

    if (tipo === "ALUNO") {
        fetch(API + "/alunos", { headers: authHeaders() })
        .then(res => res.json())
        .then(alunos => {
            const aluno = alunos.find(a => a.usuario && a.usuario.usuario === usuario.usuario);
            if (!aluno) {
                const lista = document.getElementById("listaFrequencias");
                if (lista) lista.innerHTML = "<li>Nenhuma frequência encontrada.</li>";
                return;
            }
            return fetch(API + "/frequencias/aluno/" + aluno.id, { headers: authHeaders() });
        })
        .then(res => res && res.json())
        .then(freqs => {
            if (!freqs) return;
            renderizarFrequencias(freqs);
        })
        .catch(err => console.error("Erro ao carregar frequências:", err.message));
    } else {
        const lista = document.getElementById("listaFrequencias");
        if (lista) lista.innerHTML = `
            <div class="form-busca">
                <label>ID do Aluno:</label>
                <input type="number" id="buscarFreqAlunoId" placeholder="Digite o ID do aluno">
                <button onclick="buscarFrequenciasPorAluno()">Buscar</button>
            </div>
        `;
    }
}

function buscarFrequenciasPorAluno() {
    const id = document.getElementById("buscarFreqAlunoId").value;
    if (!id) { alert("Digite o ID do aluno."); return; }
    fetch(API + "/frequencias/aluno/" + id, { headers: authHeaders() })
    .then(res => res.json())
    .then(freqs => renderizarFrequencias(freqs))
    .catch(err => console.error("Erro:", err.message));
}

function renderizarFrequencias(freqs) {
    const lista = document.getElementById("listaFrequencias");
    if (!lista) return;
    if (!freqs || freqs.length === 0) {
        lista.innerHTML = "<li>Nenhuma frequência encontrada.</li>";
        return;
    }
    lista.innerHTML = "";
    freqs.forEach(f => {
        const li = document.createElement("li");
        li.innerHTML = `
            ${f.data} — <strong>${f.disciplina?.nome || "—"}</strong>:
            ${f.presente ? "✅ Presente" : "❌ Falta"}
            ${f.observacao ? "(" + f.observacao + ")" : ""}
        `;
        lista.appendChild(li);
    });
}

// =========================
// CARREGAR AULAS (estático por enquanto)
// =========================
function carregarAulas() {
    const aulas = [
        { materia: "Matemática", horario: "08:00 - 09:00" },
        { materia: "Português",  horario: "09:00 - 10:00" }
    ];
    const lista = document.getElementById("listaAulas");
    if (!lista) return;
    lista.innerHTML = "";
    aulas.forEach(a => {
        const li = document.createElement("li");
        li.innerHTML = `<strong>${a.materia}</strong> — ${a.horario}`;
        lista.appendChild(li);
    });
}

// =========================
// CARREGAR PROFESSORES
// =========================
function carregarProfessores() {
    fetch(API + "/professores", { headers: authHeaders() })
    .then(res => res.json())
    .then(professores => {
        const lista = document.getElementById("listaProfessores");
        if (!lista) return;
        lista.innerHTML = "";
        professores.forEach(p => {
            const li = document.createElement("li");
            li.innerHTML = `<strong>${p.nome}</strong> — ${p.materia || "—"}`;
            lista.appendChild(li);
        });
    })
    .catch(err => console.error("Erro ao carregar professores:", err.message));
}

// =========================
// LANÇAR NOTA (PROFESSOR/ADMIN)
// =========================
function prepararLancarNota() {
    // Carrega selects de alunos e disciplinas
    Promise.all([
        fetch(API + "/alunos", { headers: authHeaders() }).then(r => r.json()),
        fetch(API + "/disciplinas", { headers: authHeaders() }).then(r => r.json())
    ]).then(([alunos, disciplinas]) => {
        const selAluno = document.getElementById("notaAlunoId");
        const selDisc  = document.getElementById("notaDisciplinaId");
        if (selAluno) {
            selAluno.innerHTML = "<option value=''>Selecione o aluno</option>";
            alunos.forEach(a => {
                selAluno.innerHTML += `<option value="${a.id}">${a.nome} (${a.matricula || a.id})</option>`;
            });
        }
        if (selDisc) {
            selDisc.innerHTML = "<option value=''>Selecione a disciplina</option>";
            disciplinas.forEach(d => {
                selDisc.innerHTML += `<option value="${d.id}">${d.nome}</option>`;
            });
        }
    });
}

function lancarNota() {
    const alunoId      = document.getElementById("notaAlunoId").value;
    const disciplinaId = document.getElementById("notaDisciplinaId").value;
    const bimestre     = document.getElementById("notaBimestre").value;
    const valor        = document.getElementById("notaValor").value;

    if (!alunoId || !disciplinaId || !bimestre || !valor) {
        alert("Preencha todos os campos!"); return;
    }

    fetch(API + "/notas", {
        method: "POST",
        headers: authHeaders(),
        body: JSON.stringify({ alunoId, disciplinaId, bimestre, valor })
    })
    .then(async res => {
        if (!res.ok) throw new Error(await res.text());
        return res.json();
    })
    .then(() => alert("Nota lançada com sucesso!"))
    .catch(err => alert("Erro: " + err.message));
}

// =========================
// LANÇAR FREQUÊNCIA (PROFESSOR/ADMIN)
// =========================
function prepararLancarFrequencia() {
    Promise.all([
        fetch(API + "/alunos", { headers: authHeaders() }).then(r => r.json()),
        fetch(API + "/disciplinas", { headers: authHeaders() }).then(r => r.json())
    ]).then(([alunos, disciplinas]) => {
        const selAluno = document.getElementById("freqAlunoId");
        const selDisc  = document.getElementById("freqDisciplinaId");
        if (selAluno) {
            selAluno.innerHTML = "<option value=''>Selecione o aluno</option>";
            alunos.forEach(a => {
                selAluno.innerHTML += `<option value="${a.id}">${a.nome} (${a.matricula || a.id})</option>`;
            });
        }
        if (selDisc) {
            selDisc.innerHTML = "<option value=''>Selecione a disciplina</option>";
            disciplinas.forEach(d => {
                selDisc.innerHTML += `<option value="${d.id}">${d.nome}</option>`;
            });
        }
    });
}

function lancarFrequencia() {
    const alunoId      = document.getElementById("freqAlunoId").value;
    const disciplinaId = document.getElementById("freqDisciplinaId").value;
    const data         = document.getElementById("freqData").value;
    const presente     = document.getElementById("freqPresente").value === "true";
    const observacao   = document.getElementById("freqObservacao").value;

    if (!alunoId || !disciplinaId || !data) {
        alert("Preencha todos os campos obrigatórios!"); return;
    }

    fetch(API + "/frequencias", {
        method: "POST",
        headers: authHeaders(),
        body: JSON.stringify({ alunoId, disciplinaId, data, presente, observacao })
    })
    .then(async res => {
        if (!res.ok) throw new Error(await res.text());
        return res.json();
    })
    .then(() => alert("Frequência registrada com sucesso!"))
    .catch(err => alert("Erro: " + err.message));
}

// =========================
// CRIAR TURMA (ADMIN)
// =========================
function criarTurma() {
    const nome    = document.getElementById("turmaNome").value;
    const ano     = document.getElementById("turmaAno").value;
    const periodo = document.getElementById("turmaPeriodo").value;

    if (!nome || !ano || !periodo) { alert("Preencha todos os campos!"); return; }

    fetch(API + "/turmas", {
        method: "POST",
        headers: authHeaders(),
        body: JSON.stringify({ nome, ano, periodo })
    })
    .then(async res => {
        if (!res.ok) throw new Error(await res.text());
        return res.json();
    })
    .then(() => {
        alert("Turma criada com sucesso!");
        carregarTurmas();
    })
    .catch(err => alert("Erro: " + err.message));
}

// =========================
// CRIAR DISCIPLINA (ADMIN)
// =========================
function criarDisciplina() {
    const nome    = document.getElementById("discNome").value;
    const codigo  = document.getElementById("discCodigo").value;

    if (!nome) { alert("Preencha o nome da disciplina!"); return; }

    fetch(API + "/disciplinas", {
        method: "POST",
        headers: authHeaders(),
        body: JSON.stringify({ nome, codigo })
    })
    .then(async res => {
        if (!res.ok) throw new Error(await res.text());
        return res.json();
    })
    .then(() => {
        alert("Disciplina criada com sucesso!");
        carregarDisciplinas();
    })
    .catch(err => alert("Erro: " + err.message));
}
