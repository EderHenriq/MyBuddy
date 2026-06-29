const fs = require('fs');
const path = 'docker/keycloak/realm-export.json';
const data = JSON.parse(fs.readFileSync(path, 'utf8'));

// 1. Habilitar directAccessGrantsEnabled (para o Angular logar por POST /token)
const frontendClient = data.clients.find(c => c.clientId === 'mybuddy-frontend');
if (frontendClient) {
    frontendClient.directAccessGrantsEnabled = true;
    frontendClient.publicClient = true; // Angular é public
    
    // Garantir redirecionamentos corretos para localhost:4200 (dev) e localhost:80 (docker)
    const requiredUris = [
        "http://localhost:4200/*",
        "http://localhost/*",
        "http://localhost:80/*"
    ];
    if (!frontendClient.redirectUris) {
        frontendClient.redirectUris = [];
    }
    requiredUris.forEach(uri => {
        if (!frontendClient.redirectUris.includes(uri)) {
            frontendClient.redirectUris.push(uri);
        }
    });
}

// Restaurar a política original caso ela exista (estamos não sobrescrevendo mais)

// 2. Adicionar role PETSHOP no Realm se não existir
if (!data.roles.realm.find(r => r.name === 'PETSHOP')) {
    data.roles.realm.push({
        "id": "role-petshop-id",
        "name": "PETSHOP",
        "description": "Role para Petshops",
        "composite": false,
        "clientRole": false,
        "containerId": data.id,
        "attributes": {}
    });
}

// 3. Adicionar usuários no Keycloak
const usersToAdd = [
    { email: 'admin@mybuddy.com', roles: ['ADMIN'] },
    { email: 'user@mybuddy.com', roles: ['USER'] },
    { email: 'ong@mybuddy.com', roles: ['ONG'] },
    { email: 'petshop@mybuddy.com', roles: ['PETSHOP'] }
];

usersToAdd.forEach((u, idx) => {
    if (!data.users.find(existing => existing.email === u.email)) {
        data.users.push({
            "id": "user-id-test-" + idx,
            "username": u.email,
            "email": u.email,
            "firstName": "Test",
            "lastName": "User",
            "enabled": true,
            "emailVerified": true,
            "requiredActions": [],
            "credentials": [
                {
                    "type": "password",
                    "value": "Senha123",
                    "temporary": false
                }
            ],
            "realmRoles": u.roles.concat(["default-roles-mybuddy"]),
            "clientRoles": {}
        });
    }
});

fs.writeFileSync(path, JSON.stringify(data, null, 2));
console.log("realm-export.json atualizado com sucesso!");
