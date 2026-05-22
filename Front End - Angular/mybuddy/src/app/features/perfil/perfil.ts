import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs';
import { CardPetComponent } from '@shared/components/card-pet/card-pet.component';
import { Footer } from '@shared/components/footer/footer';
import { AuthService } from '@core/services/auth.service';
import { UploadService } from '@core/services/upload.service';

type ProfileTab = 'inicio' | 'dados' | 'pets' | 'favoritos' | 'mensagens' | 'solicitacoes' | 'historico' | 'configuracoes';
type RoleKey = 'ADOTANTE' | 'ONG' | 'PETSHOP' | 'ADMIN';

interface BackendRole {
  id?: number;
  name?: string;
}

interface BackendOrganization {
  id?: number;
  nomeFantasia?: string;
  emailContato?: string;
  cnpj?: string;
  telefoneContato?: string;
  endereco?: string;
  descricao?: string;
  website?: string;
}

interface BackendUserProfile {
  id?: number;
  nome?: string;
  name?: string;
  email?: string;
  telefone?: string;
  endereco?: string;
  documento?: string;
  descricao?: string;
  website?: string;
  fotoPerfil?: string;
  aceitaMensagens?: boolean;
  perfilPublico?: boolean;
  notificacoesEmail?: boolean;
  roles?: (string | BackendRole)[];
  organizacao?: BackendOrganization | null;
  petshop?: BackendOrganization | null;
}

interface ProfileUser {
  id?: number;
  name: string;
  displayName: string;
  role: string;
  roleKey: RoleKey;
  email: string;
  phone: string;
  profilePic: string;
  acceptsMessages: boolean;
  publicProfile: boolean;
  emailNotifications: boolean;
  organization: BackendOrganization | null;
}

interface ProfilePet {
  name: string;
  age: string;
  breed: string;
  sex: string;
  vaccinated: string;
  imageUrl: string;
  badgeText: string;
  badgeType: 'adoption' | 'adopted' | '';
  isFavorite: boolean;
  showTopHeart: boolean;
}

interface ProfileMessage {
  from: string;
  preview: string;
  date: string;
  unread: boolean;
}

interface ProfileSolicitacao {
  petName: string;
  ongName: string;
  status: 'Pendente' | 'Aprovada' | 'Em Análise' | 'Recusada';
  date: string;
  imageUrl: string;
}

interface ProfilePermission {
  label: string;
  enabled: boolean;
}

interface ProfileField {
  label: string;
  icon: string;
  value: string;
}

interface ProfileRoleConfig {
  label: string;
  descriptionFallback: string;
  managesPets: boolean;
  businessProfile: boolean;
  businessNameLabel: string;
  businessDocumentLabel: string;
  businessEmailLabel: string;
  businessPhoneLabel: string;
  businessAddressLabel: string;
  businessDescriptionLabel: string;
}

import { NotificationService } from '@core/services/notification.service';
import { ActivityHistory } from '@core/models/notification.model';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CardPetComponent, Footer],
  templateUrl: './perfil.html',
  styleUrl: './perfil.scss',
})
export class Perfil implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  private readonly notificationService = inject(NotificationService);
  private readonly uploadService = inject(UploadService);
  private readonly route = inject(ActivatedRoute);

  readonly history$ = this.notificationService.history$;

  readonly activeTab = signal<ProfileTab>('inicio');
  readonly isEditing = signal(false);
  readonly isSaving = signal(false);
  readonly saveMessage = signal('');
  readonly user = signal<ProfileUser>(this.emptyUser());
  readonly profilePicPreview = signal<string | null>(null);
  selectedProfilePicFile: File | null = null;

  readonly roleConfigs: Record<RoleKey, ProfileRoleConfig> = {
    ADOTANTE: {
      label: 'Adotante',
      descriptionFallback: 'Complete seu perfil para facilitar conversas com ONGs e acompanhar pets favoritados.',
      managesPets: false,
      businessProfile: false,
      businessNameLabel: '',
      businessDocumentLabel: '',
      businessEmailLabel: '',
      businessPhoneLabel: '',
      businessAddressLabel: '',
      businessDescriptionLabel: '',
    },
    ONG: {
      label: 'ONG',
      descriptionFallback: 'Complete os dados da organização para transmitir confiança aos adotantes.',
      managesPets: true,
      businessProfile: true,
      businessNameLabel: 'Nome fantasia da ONG',
      businessDocumentLabel: 'CNPJ da ONG',
      businessEmailLabel: 'E-mail de contato da ONG',
      businessPhoneLabel: 'Telefone de contato da ONG',
      businessAddressLabel: 'Endereço da ONG',
      businessDescriptionLabel: 'Descrição da ONG',
    },
    PETSHOP: {
      label: 'Petshop',
      descriptionFallback: 'Complete os dados comerciais para divulgar serviços e receber contatos.',
      managesPets: true,
      businessProfile: true,
      businessNameLabel: 'Nome fantasia do Petshop',
      businessDocumentLabel: 'CNPJ do Petshop',
      businessEmailLabel: 'E-mail de contato do Petshop',
      businessPhoneLabel: 'Telefone de contato do Petshop',
      businessAddressLabel: 'Endereço do Petshop',
      businessDescriptionLabel: 'Descrição do Petshop',
    },
    ADMIN: {
      label: 'Administrador',
      descriptionFallback: 'Perfil administrativo com acesso ampliado para gestão da plataforma.',
      managesPets: true,
      businessProfile: false,
      businessNameLabel: '',
      businessDocumentLabel: '',
      businessEmailLabel: '',
      businessPhoneLabel: '',
      businessAddressLabel: '',
      businessDescriptionLabel: '',
    },
  };

  readonly profileForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required, Validators.minLength(8)]],
    profilePic: ['', [Validators.required]],
    organizationName: [''],
    organizationEmail: [''],
    organizationPhone: [''],
    organizationCnpj: [''],
    organizationAddress: [''],
    organizationDescription: ['', [Validators.maxLength(500)]],
    organizationWebsite: [''],
    acceptsMessages: [true],
    publicProfile: [true],
    emailNotifications: [true],
  });

  readonly navItems: { id: ProfileTab; label: string; icon: string }[] = [
    { id: 'inicio', label: 'Início do Perfil', icon: 'fas fa-home' },
    { id: 'dados', label: 'Meus Dados', icon: 'far fa-user' },
    { id: 'pets', label: 'Meus Pets', icon: 'fas fa-paw' },
    { id: 'favoritos', label: 'Meus Favoritos', icon: 'far fa-heart' },
    { id: 'mensagens', label: 'Minhas Mensagens', icon: 'far fa-comment-dots' },
    { id: 'solicitacoes', label: 'Minhas Solicitações', icon: 'fas fa-clipboard-list' },
    { id: 'historico', label: 'Histórico', icon: 'fas fa-history' },
    { id: 'configuracoes', label: 'Configurações', icon: 'fas fa-cog' },
  ];

  readonly buddies = signal<ProfilePet[]>([
    {
      name: 'Paçoca',
      age: '5 anos',
      breed: 'Vira-lata',
      sex: 'Macho',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&q=80&w=500',
      badgeText: 'Em processo de adoção',
      badgeType: 'adoption',
      isFavorite: true,
      showTopHeart: false,
    },
    {
      name: 'Jade',
      age: '1 ano',
      breed: 'Vira-lata',
      sex: 'Fêmea',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?auto=format&fit=crop&q=80&w=500',
      badgeText: 'Adotado',
      badgeType: 'adopted',
      isFavorite: true,
      showTopHeart: false,
    },
  ]);

  readonly favorites = signal<ProfilePet[]>([
    {
      name: 'Nevasca',
      age: '3 anos',
      breed: 'Persa',
      sex: 'Fêmea',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1618826411640-d6df44dd3f7a?auto=format&fit=crop&q=80&w=500',
      badgeText: '',
      badgeType: '',
      isFavorite: true,
      showTopHeart: true,
    },
    {
      name: 'Thor',
      age: '4 anos',
      breed: 'Border Collie',
      sex: 'Macho',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&q=80&w=500',
      badgeText: '',
      badgeType: '',
      isFavorite: true,
      showTopHeart: true,
    },
  ]);

  readonly messages = signal<ProfileMessage[]>([
    {
      from: 'Equipe MyBuddy',
      preview: 'Seu perfil está pronto para receber recomendações.',
      date: 'Hoje',
      unread: true,
    },
    {
      from: 'Cão Sem Dono',
      preview: 'Recebemos sua solicitação de contato sobre a Jade.',
      date: 'Ontem',
      unread: false,
    },
  ]);

  readonly solicitacoes = signal<ProfileSolicitacao[]>([
    {
      petName: 'Paçoca',
      ongName: 'Abrigo Animal',
      status: 'Em Análise',
      date: '10/05/2026',
      imageUrl: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&q=80&w=500',
    },
    {
      petName: 'Rex',
      ongName: 'Cão Sem Dono',
      status: 'Pendente',
      date: '18/05/2026',
      imageUrl: 'https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&q=80&w=500',
    },
  ]);

  readonly roleConfig = computed(() => this.roleConfigs[this.user().roleKey]);
  readonly isBusinessProfile = computed(() => this.roleConfig().businessProfile);
  readonly canManagePets = computed(() => this.roleConfig().managesPets);

  readonly userFields = computed<ProfileField[]>(() => [
    { label: 'Nome', icon: 'far fa-user', value: this.user().name },
    { label: 'E-mail', icon: 'far fa-envelope', value: this.user().email },
    { label: 'Telefone', icon: 'fas fa-phone-alt', value: this.user().phone },
  ]);

  readonly roleFields = computed<ProfileField[]>(() => {
    const org = this.user().organization;
    if (!this.isBusinessProfile() || !org) {
      return [];
    }

    return [
      { label: this.roleConfig().businessNameLabel, icon: 'fas fa-store', value: org.nomeFantasia || '' },
      { label: this.roleConfig().businessDocumentLabel, icon: 'far fa-id-card', value: org.cnpj || '' },
      { label: this.roleConfig().businessEmailLabel, icon: 'far fa-envelope', value: org.emailContato || '' },
      { label: this.roleConfig().businessPhoneLabel, icon: 'fas fa-phone-alt', value: org.telefoneContato || '' },
      { label: this.roleConfig().businessAddressLabel, icon: 'fas fa-map-marker-alt', value: org.endereco || '' },
      { label: 'Website', icon: 'fas fa-link', value: org.website || '' },
    ].filter(field => field.value);
  });

  readonly profileDescription = computed(() => this.user().organization?.descricao || this.roleConfig().descriptionFallback);

  readonly rolePermissions = computed<ProfilePermission[]>(() => {
    const permissionsByRole: Record<RoleKey, ProfilePermission[]> = {
      ADOTANTE: [
        { label: 'Favoritar pets', enabled: true },
        { label: 'Enviar mensagens para ONGs e petshops', enabled: true },
        { label: 'Gerenciar pets para adoção', enabled: false },
        { label: 'Administrar usuários', enabled: false },
      ],
      ONG: [
        { label: 'Gerenciar pets para adoção', enabled: true },
        { label: 'Receber contatos de adotantes', enabled: true },
        { label: 'Publicar perfil institucional', enabled: true },
        { label: 'Administrar usuários', enabled: false },
      ],
      PETSHOP: [
        { label: 'Gerenciar pets parceiros', enabled: true },
        { label: 'Receber contatos comerciais', enabled: true },
        { label: 'Publicar perfil comercial', enabled: true },
        { label: 'Administrar usuários', enabled: false },
      ],
      ADMIN: [
        { label: 'Administrar usuários', enabled: true },
        { label: 'Gerenciar organizações', enabled: true },
        { label: 'Gerenciar pets da plataforma', enabled: true },
        { label: 'Acompanhar interesses de adoção', enabled: true },
      ],
    };

    return permissionsByRole[this.user().roleKey];
  });

  readonly stats = computed(() => [
    { label: this.canManagePets() ? 'Pets gerenciados' : 'Buddies acompanhados', value: this.buddies().length },
    { label: 'Favoritos', value: this.favorites().length },
    { label: 'Mensagens não lidas', value: this.messages().filter(message => message.unread).length },
  ]);

  get buddiesTitle(): string {
    if (this.user().roleKey === 'ADMIN') return 'Pets da plataforma';
    if (this.canManagePets()) return 'Animais disponíveis';
    return 'Meus buddies';
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['tab']) {
        this.selectTab(params['tab'] as ProfileTab);
      }
    });

    const profile = this.authService.currentUser();
    if (profile) {
      this.setupProfile(profile);
      return;
    }

    this.authService.getProfile().subscribe(data => {
      this.setupProfile(data);
    });
  }

  selectTab(tab: ProfileTab): void {
    this.activeTab.set(tab);
    this.saveMessage.set('');
  }

  startEditing(): void {
    this.isEditing.set(true);
    this.selectTab('dados');
    this.applyRoleValidators();
    this.patchForm(this.user());
    this.profilePicPreview.set(null);
    this.selectedProfilePicFile = null;
  }

  cancelEditing(): void {
    this.isEditing.set(false);
    this.saveMessage.set('');
    this.profilePicPreview.set(null);
    this.selectedProfilePicFile = null;
    this.patchForm(this.user());
  }

  onProfilePicChange(event: any): void {
    if (event.target.files && event.target.files[0]) {
      this.selectedProfilePicFile = event.target.files[0];
      const reader = new FileReader();
      reader.onload = e => {
        this.profilePicPreview.set(reader.result as string);
        this.profileForm.controls.profilePic.setValue('pending-upload');
      };
      reader.readAsDataURL(this.selectedProfilePicFile!);
    }
  }

  logout(): void {
    this.authService.logout();
  }

  saveProfile(): void {
    this.applyRoleValidators();

    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    const formValue = this.profileForm.getRawValue();
    const organizationPayload: BackendOrganization | null = this.isBusinessProfile()
      ? {
          id: this.user().organization?.id,
          nomeFantasia: formValue.organizationName,
          emailContato: formValue.organizationEmail,
          cnpj: formValue.organizationCnpj,
          telefoneContato: formValue.organizationPhone,
          endereco: formValue.organizationAddress,
          descricao: formValue.organizationDescription,
          website: formValue.organizationWebsite,
        }
      : null;

    this.isSaving.set(true);
    if (this.selectedProfilePicFile) {
      this.uploadService.uploadImage(this.selectedProfilePicFile).subscribe({
        next: url => {
          this.selectedProfilePicFile = null;
          formValue.profilePic = url;
          this.executeProfileUpdate(formValue, organizationPayload);
        },
        error: () => {
          this.isSaving.set(false);
          this.saveMessage.set('Erro ao fazer upload da foto de perfil.');
        },
      });
    } else {
      this.executeProfileUpdate(formValue, organizationPayload);
    }
  }

  private executeProfileUpdate(formValue: any, organizationPayload: any): void {
    const payload = {
      id: this.user().id,
      nome: formValue.name,
      email: formValue.email,
      telefone: formValue.phone,
      fotoPerfil: formValue.profilePic,
      aceitaMensagens: formValue.acceptsMessages,
      perfilPublico: formValue.publicProfile,
      notificacoesEmail: formValue.emailNotifications,
      organizacao: organizationPayload,
      nomeFantasia: organizationPayload?.nomeFantasia,
      emailContato: organizationPayload?.emailContato,
      telefoneContato: organizationPayload?.telefoneContato,
      cnpj: organizationPayload?.cnpj,
      endereco: organizationPayload?.endereco,
      descricao: organizationPayload?.descricao,
      website: organizationPayload?.website,
    };

    this.authService
      .updateProfile(payload)
      .pipe(finalize(() => this.isSaving.set(false)))
      .subscribe({
        next: updatedProfile => {
          this.setupProfile(updatedProfile);
          this.isEditing.set(false);
          this.saveMessage.set('Perfil atualizado com sucesso.');
        },
        error: () => {
          this.saveMessage.set('Não foi possível salvar agora. Tente novamente em instantes.');
        },
      });
  }

  removeFavorite(petName: string): void {
    this.favorites.update(pets => pets.filter(pet => pet.name !== petName));
  }

  markAllMessagesAsRead(): void {
    this.messages.update(messages => messages.map(message => ({ ...message, unread: false })));
  }

  isFieldInvalid(field: keyof typeof this.profileForm.controls): boolean {
    const control = this.profileForm.controls[field];
    return control.invalid && (control.dirty || control.touched);
  }

  private setupProfile(profile: BackendUserProfile): void {
    const roles = this.normalizeRoles(profile.roles);
    const roleKey = this.resolveRoleKey(roles);
    const organization = profile.organizacao ?? profile.petshop ?? null;
    const nextUser: ProfileUser = {
      id: profile.id,
      name: profile.nome || profile.name || 'Usuário MyBuddy',
      displayName: this.resolveDisplayName(roleKey, profile, organization),
      email: profile.email || '',
      phone: profile.telefone || '',
      role: this.roleConfigs[roleKey].label,
      roleKey,
      profilePic: profile.fotoPerfil || this.resolveDefaultPicture(roleKey),
      acceptsMessages: profile.aceitaMensagens ?? true,
      publicProfile: profile.perfilPublico ?? true,
      emailNotifications: profile.notificacoesEmail ?? true,
      organization,
    };

    this.user.set(nextUser);
    this.applyRoleValidators();
    this.patchForm(nextUser);
  }

  private patchForm(user: ProfileUser): void {
    this.profileForm.reset({
      name: user.name,
      email: user.email,
      phone: user.phone,
      profilePic: user.profilePic,
      organizationName: user.organization?.nomeFantasia || '',
      organizationEmail: user.organization?.emailContato || '',
      organizationPhone: user.organization?.telefoneContato || '',
      organizationCnpj: user.organization?.cnpj || '',
      organizationAddress: user.organization?.endereco || '',
      organizationDescription: user.organization?.descricao || '',
      organizationWebsite: user.organization?.website || '',
      acceptsMessages: user.acceptsMessages,
      publicProfile: user.publicProfile,
      emailNotifications: user.emailNotifications,
    });
  }

  private applyRoleValidators(): void {
    const businessValidators: Partial<Record<keyof typeof this.profileForm.controls, ValidatorFn[]>> = {
      organizationName: [Validators.required, Validators.minLength(3)],
      organizationEmail: [Validators.required, Validators.email],
      organizationPhone: [Validators.required, Validators.minLength(8)],
      organizationCnpj: [Validators.required, Validators.minLength(14)],
      organizationAddress: [Validators.required, Validators.minLength(5)],
    };

    Object.entries(businessValidators).forEach(([field, validators]) => {
      const control = this.profileForm.controls[field as keyof typeof this.profileForm.controls];
      if (this.isBusinessProfile()) {
        control.setValidators(validators);
      } else {
        control.clearValidators();
      }
      control.updateValueAndValidity({ emitEvent: false });
    });
  }

  private normalizeRoles(roles: BackendUserProfile['roles']): string[] {
    if (!Array.isArray(roles)) {
      return this.authService.getUserRoles();
    }

    return roles.map(role => {
      if (typeof role === 'string') return this.normalizeRoleName(role);
      if (role?.name) return this.normalizeRoleName(role.name);
      return '';
    });
  }

  private normalizeRoleName(role: string): string {
    const upperRole = role.toUpperCase();
    return upperRole.startsWith('ROLE_') ? upperRole : `ROLE_${upperRole}`;
  }

  private resolveRoleKey(roles: string[]): RoleKey {
    if (roles.includes('ROLE_ADMIN')) return 'ADMIN';
    if (roles.includes('ROLE_ONG')) return 'ONG';
    if (roles.includes('ROLE_PETSHOP')) return 'PETSHOP';
    return 'ADOTANTE';
  }

  private resolveDisplayName(role: RoleKey, profile: BackendUserProfile, organization: BackendOrganization | null): string {
    if (this.roleConfigs[role].businessProfile) {
      return organization?.nomeFantasia || profile.nome || 'Usuário MyBuddy';
    }

    return profile.nome || profile.name || 'Usuário MyBuddy';
  }

  private resolveDefaultPicture(role: RoleKey): string {
    const pictures: Record<RoleKey, string> = {
      ADOTANTE: 'https://images.unsplash.com/photo-1531123897727-8f129e1688ce?auto=format&fit=crop&q=80&w=300',
      ONG: 'https://images.unsplash.com/photo-1542382257-80da9fb9f5c4?auto=format&fit=crop&q=80&w=300',
      PETSHOP: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=300',
      ADMIN: 'https://images.unsplash.com/photo-1556157382-97eda2d62296?auto=format&fit=crop&q=80&w=300',
    };
    return pictures[role];
  }

  private emptyUser(): ProfileUser {
    return {
      name: 'Carregando...',
      displayName: 'Carregando...',
      role: '',
      roleKey: 'ADOTANTE',
      email: '',
      phone: '',
      profilePic: 'https://images.unsplash.com/photo-1531123897727-8f129e1688ce?auto=format&fit=crop&q=80&w=300',
      acceptsMessages: true,
      publicProfile: true,
      emailNotifications: true,
      organization: null,
    };
  }
}
