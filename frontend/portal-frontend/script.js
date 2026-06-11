const API = "http://localhost:8080/api";

function getToken() { return localStorage.getItem("token"); }
function getUsuario() { const u = localStorage.getItem("usuarioLogado"); return u ? JSON.parse(u) : null; }
function authHeaders() {
    return { "Content-Type": "application/json", "Authorization": "Bearer " + getToken() };
}

// LOGIN
function login() {
    const usuario = document.getElementById("loginUsuario").value.trim();
    const senha   = document.getElementById("loginSenha").value.trim();
    if (!usuario || !senha) { alert("Preencha todos os campos!"); return; }
    fetch(API + "/login", {
        method: "POST", headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ usuario, senha })
    })
    .then(async res => { if (!res.ok) throw new Error("Usuário ou senha inválidos!"); return res.json(); })
    .then(data => {
        localStorage.setItem("token", data.token);
        localStorage.setItem("usuarioLogado", JSON.stringify({ id: data.id, nome: data.nome, usuario: data.usuario, tipo: data.tipo }));
        const tipo = (data.tipo || "").toUpperCase();
        if (tipo === "PROFESSOR")        window.location.href = "professor.html";
        else if (tipo === "ADMIN")       window.location.href = "admin.html";
        else if (tipo === "RESPONSAVEL") window.location.href = "responsavel.html";
        else                             window.location.href = "aluno.html";
    })
    .catch(err => alert("❌ " + err.message));
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("usuarioLogado");
    window.location.href = "index.html";
}

function mostrarUsuario() {
    const u = getUsuario();
    if (!u) return;
    const el = document.getElementById("usuarioLogado");
    const tp = document.getElementById("tipoUsuario");
    if (el) el.innerText = u.nome;
    if (tp) tp.innerText = u.tipo;
}

function mostrarSecao(secao) {
    document.querySelectorAll("section").forEach(s => s.classList.remove("active"));
    document.querySelectorAll(".sidebar nav li").forEach(l => l.classList.remove("active"));
    const alvo = document.getElementById(secao);
    if (alvo) alvo.classList.add("active");
    const nav = document.querySelector(`[data-secao="${secao}"]`);
    if (nav) nav.classList.add("active");
    const carregadores = {
        turmas: carregarTurmas, disciplinas: carregarDisciplinas,
        alunos: carregarAlunos, professores: carregarProfessores,
        notas: carregarNotas, frequencias: carregarFrequencias,
        usuarios: carregarUsuarios, trabalhos: carregarTrabalhos,
        avaliacoes: carregarAvaliacoes, advertencias: carregarAdvertencias,
        boletim: carregarBoletim
    };
    if (carregadores[secao]) carregadores[secao]();
}

// CADASTRO
function salvarUsuario(usuario) {
    fetch(API + "/cadastro", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(usuario) })
    .then(async res => { if (!res.ok) { const err = await res.text(); throw new Error(err.includes("já existe") ? "Usuário já cadastrado!" : "Erro ao cadastrar!"); } return res.json(); })
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

// LISTAS GERAIS
function carregarTurmas() {
    fetch(API + "/turmas", { headers: authHeaders() }).then(r => r.json()).then(turmas => {
        const lista = document.getElementById("listaTurmas");
        if (!lista) return;
        lista.innerHTML = turmas.length ? turmas.map(t => `<li>🏫 <strong>${t.nome}</strong> — ${t.ano} | <span class="badge badge-blue">${t.periodo}</span></li>`).join("") : '<li>Nenhuma turma cadastrada</li>';
    }).catch(() => {});
}

function carregarDisciplinas() {
    fetch(API + "/disciplinas", { headers: authHeaders() }).then(r => r.json()).then(disciplinas => {
        const lista = document.getElementById("listaDisciplinas");
        if (!lista) return;
        lista.innerHTML = disciplinas.length ? disciplinas.map(d => `<li>📚 <strong>${d.nome}</strong> <span class="badge badge-blue">${d.codigo || ""}</span></li>`).join("") : '<li>Nenhuma disciplina</li>';
    }).catch(() => {});
}

function carregarAlunos() {
    fetch(API + "/alunos", { headers: authHeaders() }).then(r => r.json()).then(alunos => {
        const lista = document.getElementById("listaAlunos");
        if (!lista) return;
        lista.innerHTML = alunos.length ? alunos.map(a => `<li>🎓 <strong>${a.nome}</strong> — ID: ${a.id} | Matrícula: ${a.matricula || "—"}</li>`).join("") : '<li>Nenhum aluno</li>';
    }).catch(() => {});
}

function carregarProfessores() {
    fetch(API + "/professores", { headers: authHeaders() }).then(r => r.json()).then(professores => {
        const lista = document.getElementById("listaProfessores");
        if (!lista) return;
        lista.innerHTML = professores.length ? professores.map(p => `<li>👨‍🏫 <strong>${p.nome}</strong> — ID: ${p.id} | ${p.materia || "—"}</li>`).join("") : '<li>Nenhum professor</li>';
    }).catch(() => {});
}

function carregarUsuarios() {
    fetch(API + "/usuarios", { headers: authHeaders() }).then(r => r.json()).then(usuarios => {
        const lista = document.getElementById("listaUsuarios");
        if (!lista) return;
        lista.innerHTML = usuarios.map(u => `<tr><td>${u.nome}</td><td>${u.usuario}</td><td><span class="badge badge-blue">${u.tipo}</span></td></tr>`).join("");
    }).catch(() => {});
}

// TRABALHOS
function carregarTrabalhos() {
    fetch(API + "/trabalhos", { headers: authHeaders() }).then(r => r.json()).then(trabalhos => {
        const lista = document.getElementById("listaTrabalhos");
        if (!lista) return;
        lista.innerHTML = trabalhos.length ? trabalhos.map(t => {
            const hoje = new Date();
            const prazo = new Date(t.prazo);
            const atrasado = prazo < hoje;
            return `<li>
                📋 <strong>${t.titulo}</strong>
                <span class="badge badge-blue">${t.disciplina?.nome || "—"}</span>
                <span class="badge ${atrasado ? "badge-red" : "badge-green"}">Prazo: ${t.prazo}</span>
                ${t.bimestre ? `<span class="badge badge-yellow">${t.bimestre}º Bim</span>` : ""}
                ${t.descricao ? `<br><small style="color:#888;margin-left:20px">${t.descricao}</small>` : ""}
            </li>`;
        }).join("") : '<li>Nenhum trabalho cadastrado</li>';
    }).catch(() => {});
}

// AVALIAÇÕES
function carregarAvaliacoes() {
    const u = getUsuario();
    if (!u || !u.id) return;
    fetch(API + "/avaliacoes/aluno/" + u.id, { headers: authHeaders() }).then(r => r.json()).then(avaliacoes => {
        const lista = document.getElementById("listaAvaliacoes");
        if (!lista) return;
        lista.innerHTML = avaliacoes.length ? avaliacoes.map(av => {
            const icone = av.tipo === "PROVA" ? "📝" : av.tipo === "TRABALHO" ? "📋" : "🙋";
            const cls = av.valor >= 7 ? "aprovado" : av.valor >= 5 ? "neutro" : "reprovado";
            return `<div class="nota-display">
                <div class="nota-circle ${cls}">${av.valor?.toFixed(1)}</div>
                <div class="nota-info">
                    <strong>${av.disciplina?.nome || "—"} — ${av.tipo} ${icone}</strong>
                    <span>${av.bimestre}º Bimestre${av.descricao ? " | " + av.descricao : ""}</span>
                </div>
            </div>`;
        }).join("") : '<div class="empty-state"><span>📝</span><p>Nenhuma avaliação lançada</p></div>';
    }).catch(() => {});
}

// ADVERTÊNCIAS
function carregarAdvertencias() {
    const u = getUsuario();
    if (!u || !u.id) return;
    fetch(API + "/advertencias/aluno/" + u.id, { headers: authHeaders() }).then(r => r.json()).then(advs => {
        const lista = document.getElementById("listaAdvertencias");
        if (!lista) return;
        lista.innerHTML = advs.length ? advs.map(a => {
            const cor = a.gravidade === "GRAVE" ? "badge-red" : a.gravidade === "MEDIA" ? "badge-yellow" : "badge-blue";
            return `<li>⚠️ <strong>${a.descricao}</strong> — ${a.data} <span class="badge ${cor}">${a.gravidade}</span></li>`;
        }).join("") : '<li>Nenhuma advertência registrada ✅</li>';
    }).catch(() => {});
}

// FREQUÊNCIAS
function carregarFrequencias() {
    const u = getUsuario();
    if (!u || !u.id) return;
    fetch(API + "/frequencias/aluno/" + u.id, { headers: authHeaders() }).then(r => r.json()).then(freqs => {
        const lista = document.getElementById("listaFrequencias");
        if (!lista) return;
        lista.innerHTML = freqs.length ? freqs.map(f => `
            <li>${f.presente ? "✅" : "❌"} <strong>${f.disciplina?.nome || "—"}</strong> — ${f.data}
            ${f.observacao ? '<span class="badge badge-yellow">' + f.observacao + '</span>' : ""}</li>
        `).join("") : '<li>Nenhum registro de frequência</li>';
    }).catch(() => {});
}

function carregarNotas() { carregarAvaliacoes(); }

// BOLETIM — para aluno e responsável consultarem por aluno/disciplina
function carregarBoletim(alunoId) {
    if (!alunoId) { const u = getUsuario(); if (u) alunoId = u.id; }
    if (!alunoId) return;

    fetch(API + "/avaliacoes/aluno/" + alunoId, { headers: authHeaders() }).then(r => r.json()).then(avaliacoes => {
        const container = document.getElementById("boletimContainer");
        if (!container) return;
        if (!avaliacoes.length) { container.innerHTML = '<p style="color:#888;text-align:center;padding:20px">Nenhuma avaliação lançada ainda.</p>'; return; }

        // Agrupa por disciplina e bimestre
        const agrupado = {};
        avaliacoes.forEach(av => {
            const disc = av.disciplina?.nome || "—";
            if (!agrupado[disc]) agrupado[disc] = { 1: [], 2: [], 3: [], 4: [] };
            agrupado[disc][av.bimestre]?.push(av);
        });

        let html = '<table><thead><tr><th>Disciplina</th><th>1º Bim</th><th>2º Bim</th><th>3º Bim</th><th>4º Bim</th><th>Média</th><th>Situação</th></tr></thead><tbody>';

        Object.entries(agrupado).forEach(([disc, bims]) => {
            const medias = [1,2,3,4].map(b => {
                const notas = bims[b];
                if (!notas.length) return null;
                return notas.reduce((s, n) => s + n.valor, 0) / notas.length;
            });

            const mediasValidas = medias.filter(m => m !== null);
            const mediaFinal = mediasValidas.length ? (mediasValidas.reduce((s,m) => s+m, 0) / mediasValidas.length) : null;
            const aprovado = mediaFinal !== null && mediaFinal >= 6;

            html += `<tr>
                <td><strong>${disc}</strong></td>
                ${medias.map(m => `<td>${m !== null ? m.toFixed(1) : "—"}</td>`).join("")}
                <td><strong>${mediaFinal !== null ? mediaFinal.toFixed(1) : "—"}</strong></td>
                <td><span class="badge ${mediaFinal === null ? "badge-blue" : aprovado ? "badge-green" : "badge-red"}">${mediaFinal === null ? "Em andamento" : aprovado ? "APROVADO" : "REPROVADO"}</span></td>
            </tr>`;
        });

        html += '</tbody></table>';
        container.innerHTML = html;
    }).catch(() => {});
}
