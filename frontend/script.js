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
        // Salva token e dados do usuário separadamente
        localStorage.setItem("token", data.token);
        localStorage.setItem("usuarioLogado", JSON.stringify({
            nome:    data.nome,
            usuario: data.usuario,
            tipo:    data.tipo
        }));

        // Redireciona pelo tipo
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
    const secoes = ["dashboard", "usuarios", "aulas", "professores", "notas", "turmas", "frequencias"];
    secoes.forEach(s => {
        const el = document.getElementById(s);
        if (el) el.style.display = "none";
    });

    const alvo = document.getElementById(secao);
    if (alvo) alvo.style.display = "block";

    // Carrega dados da seção ao abrir
    if (secao === "usuarios")     carregarUsuarios();
    if (secao === "aulas")        carregarAulas();
    if (secao === "professores")  carregarProfessores();
    if (secao === "notas")        carregarNotas();
    if (secao === "turmas")       carregarTurmas();
    if (secao === "frequencias")  carregarFrequencias();
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
// CARREGAR NOTAS DO ALUNO LOGADO
// =========================
function carregarNotas() {
    const usuario = getUsuario();
    if (!usuario) return;

    // Busca o id do aluno pelo usuário logado — ajuste conforme sua lógica
    fetch(API + "/notas/aluno/" + usuario.id, { headers: authHeaders() })
    .then(res => res.json())
    .then(notas => {
        const lista = document.getElementById("listaNotas");
        if (!lista) return;
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
    })
    .catch(err => console.error("Erro ao carregar notas:", err.message));
}

// =========================
// CARREGAR FREQUÊNCIAS DO ALUNO LOGADO
// =========================
function carregarFrequencias() {
    const usuario = getUsuario();
    if (!usuario) return;

    fetch(API + "/frequencias/aluno/" + usuario.id, { headers: authHeaders() })
    .then(res => res.json())
    .then(freqs => {
        const lista = document.getElementById("listaFrequencias");
        if (!lista) return;
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
    })
    .catch(err => console.error("Erro ao carregar frequências:", err.message));
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
