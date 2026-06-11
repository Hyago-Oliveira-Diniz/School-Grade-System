const API = "http://localhost:8080/api";

// =========================
// HELPERS
// =========================
function getToken() { return localStorage.getItem("token"); }
function getUsuario() { const u = localStorage.getItem("usuarioLogado"); return u ? JSON.parse(u) : null; }
function authHeaders() {
    return { "Content-Type": "application/json", "Authorization": "Bearer " + getToken() };
}

// =========================
// LOGIN
// =========================
function login() {
    const usuario = document.getElementById("loginUsuario").value.trim();
    const senha   = document.getElementById("loginSenha").value.trim();
    if (!usuario || !senha) { alert("Preencha todos os campos!"); return; }

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
            id: data.id, nome: data.nome, usuario: data.usuario, tipo: data.tipo
        }));
        const tipo = (data.tipo || "").toUpperCase();
        if (tipo === "PROFESSOR")    window.location.href = "professor.html";
        else if (tipo === "ADMIN")   window.location.href = "admin.html";
        else if (tipo === "RESPONSAVEL") window.location.href = "responsavel.html";
        else                         window.location.href = "aluno.html";
    })
    .catch(err => alert("❌ " + err.message));
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
// MOSTRAR USUÁRIO
// =========================
function mostrarUsuario() {
    const u = getUsuario();
    if (!u) return;
    const el = document.getElementById("usuarioLogado");
    const tp = document.getElementById("tipoUsuario");
    if (el) el.innerText = u.nome;
    if (tp) tp.innerText = u.tipo;
}

// =========================
// MOSTRAR SEÇÃO
// =========================
function mostrarSecao(secao) {
    document.querySelectorAll("section").forEach(s => s.classList.remove("active"));
    document.querySelectorAll(".sidebar nav li").forEach(l => l.classList.remove("active"));

    const alvo = document.getElementById(secao);
    if (alvo) alvo.classList.add("active");

    const nav = document.querySelector(`[data-secao="${secao}"]`);
    if (nav) nav.classList.add("active");

    if (secao === "turmas")       carregarTurmas();
    if (secao === "disciplinas")  carregarDisciplinas();
    if (secao === "alunos")       carregarAlunos();
    if (secao === "professores")  carregarProfessores();
    if (secao === "notas")        carregarNotas();
    if (secao === "frequencias")  carregarFrequencias();
    if (secao === "usuarios")     carregarUsuarios();
}

// =========================
// CADASTRO
// =========================
function salvarUsuario(usuario) {
    fetch(API + "/cadastro", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(usuario)
    })
    .then(async res => {
        if (!res.ok) {
            const err = await res.text();
            throw new Error(err.includes("já existe") ? "Usuário já cadastrado!" : "Erro ao cadastrar!");
        }
        return res.json();
    })
    .then(() => { alert("✅ Cadastro realizado!"); window.location.href = "index.html"; })
    .catch(err => alert("❌ " + err.message));
}

function cadastrarProfessor() {
    const nome = document.getElementById("nome").value.trim();
    const usuario = document.getElementById("usuario").value.trim();
    const senha = document.getElementById("senha").value.trim();
    const materia = document.getElementById("materia").value.trim();
    const registro = document.getElementById("registro").value.trim();
    if (!nome || !usuario || !senha || !materia) { alert("Preencha os campos obrigatórios!"); return; }
    salvarUsuario({ nome, usuario, senha, tipo: "PROFESSOR", materia, registro });
}

function cadastrarAluno() {
    const nome = document.getElementById("nome").value.trim();
    const usuario = document.getElementById("usuario").value.trim();
    const senha = document.getElementById("senha").value.trim();
    const matricula = document.getElementById("matricula").value.trim();
    if (!nome || !usuario || !senha) { alert("Preencha os campos obrigatórios!"); return; }
    salvarUsuario({ nome, usuario, senha, tipo: "ALUNO", matricula });
}

function cadastrarResponsavel() {
    const nome = document.getElementById("nome").value.trim();
    const usuario = document.getElementById("usuario").value.trim();
    const senha = document.getElementById("senha").value.trim();
    if (!nome || !usuario || !senha) { alert("Preencha os campos obrigatórios!"); return; }
    salvarUsuario({ nome, usuario, senha, tipo: "RESPONSAVEL" });
}

// =========================
// CARREGAR DADOS
// =========================
function carregarTurmas() {
    fetch(API + "/turmas", { headers: authHeaders() })
    .then(res => res.json())
    .then(turmas => {
        const lista = document.getElementById("listaTurmas");
        if (!lista) return;
        if (!turmas.length) { lista.innerHTML = '<li class="empty-state"><span>🏫</span><p>Nenhuma turma cadastrada</p></li>'; return; }
        lista.innerHTML = turmas.map(t => `
            <li>🏫 <strong>${t.nome}</strong> — ${t.ano} | <span class="badge badge-blue">${t.periodo}</span></li>
        `).join("");
    }).catch(() => {});
}

function carregarDisciplinas() {
    fetch(API + "/disciplinas", { headers: authHeaders() })
    .then(res => res.json())
    .then(disciplinas => {
        const lista = document.getElementById("listaDisciplinas");
        if (!lista) return;
        if (!disciplinas.length) { lista.innerHTML = '<li>Nenhuma disciplina cadastrada</li>'; return; }
        lista.innerHTML = disciplinas.map(d => `
            <li>📚 <strong>${d.nome}</strong> <span class="badge badge-blue">${d.codigo || ""}</span></li>
        `).join("");
    }).catch(() => {});
}

function carregarAlunos() {
    fetch(API + "/alunos", { headers: authHeaders() })
    .then(res => res.json())
    .then(alunos => {
        const lista = document.getElementById("listaAlunos");
        if (!lista) return;
        if (!alunos.length) { lista.innerHTML = '<li>Nenhum aluno cadastrado</li>'; return; }
        lista.innerHTML = alunos.map(a => `
            <li>🎓 <strong>${a.nome}</strong> — Matrícula: ${a.matricula || "—"}</li>
        `).join("");
    }).catch(() => {});
}

function carregarProfessores() {
    fetch(API + "/professores", { headers: authHeaders() })
    .then(res => res.json())
    .then(professores => {
        const lista = document.getElementById("listaProfessores");
        if (!lista) return;
        if (!professores.length) { lista.innerHTML = '<li>Nenhum professor cadastrado</li>'; return; }
        lista.innerHTML = professores.map(p => `
            <li>👨‍🏫 <strong>${p.nome}</strong> — ${p.materia || "—"}</li>
        `).join("");
    }).catch(() => {});
}

function carregarNotas() {
    const u = getUsuario();
    if (!u || !u.id) return;
    fetch(API + "/notas/aluno/" + u.id, { headers: authHeaders() })
    .then(res => res.json())
    .then(notas => {
        const lista = document.getElementById("listaNotas");
        if (!lista) return;
        if (!notas.length) { lista.innerHTML = '<div class="empty-state"><span>📝</span><p>Nenhuma nota lançada ainda</p></div>'; return; }
        lista.innerHTML = notas.map(n => {
            const v = n.valor || 0;
            const cls = v >= 7 ? "aprovado" : v >= 5 ? "neutro" : "reprovado";
            return `
            <div class="nota-display">
                <div class="nota-circle ${cls}">${v.toFixed(1)}</div>
                <div class="nota-info">
                    <strong>${n.disciplina?.nome || "Disciplina"}</strong>
                    <span>${n.bimestre ? n.bimestre + "º Bimestre" : ""} ${n.notaFinal ? "| Média: " + n.notaFinal : ""}</span>
                </div>
            </div>`;
        }).join("");
    }).catch(() => {});
}

function carregarFrequencias() {
    const u = getUsuario();
    if (!u || !u.id) return;
    fetch(API + "/frequencias/aluno/" + u.id, { headers: authHeaders() })
    .then(res => res.json())
    .then(freqs => {
        const lista = document.getElementById("listaFrequencias");
        if (!lista) return;
        if (!freqs.length) { lista.innerHTML = '<li>Nenhum registro de frequência</li>'; return; }
        lista.innerHTML = freqs.map(f => `
            <li>
                ${f.presente ? "✅" : "❌"}
                <strong>${f.disciplina?.nome || "—"}</strong>
                — ${f.data}
                ${f.observacao ? '<span class="badge badge-yellow">' + f.observacao + '</span>' : ""}
            </li>
        `).join("");
    }).catch(() => {});
}

function carregarUsuarios() {
    fetch(API + "/usuarios", { headers: authHeaders() })
    .then(res => res.json())
    .then(usuarios => {
        const lista = document.getElementById("listaUsuarios");
        if (!lista) return;
        lista.innerHTML = usuarios.map(u => `
            <tr>
                <td>${u.nome}</td>
                <td>${u.usuario}</td>
                <td><span class="badge badge-blue">${u.tipo}</span></td>
            </tr>
        `).join("");
    }).catch(() => {});
}
