import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { User, UpdateProfileDTO, ChangePasswordDTO } from '../../models/user.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class ProfileComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  profileForm!: FormGroup;
  passwordForm!: FormGroup;
  
  isEditingProfile = false;
  isChangingPassword = false;
  isLoading = false;
  successMessage = '';
  errorMessage = '';
  
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.initializeForms();
  }

  ngOnInit(): void {
    this.loadUserProfile();
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
        if (user) {
          this.profileForm.patchValue({
            firstName: user.firstName,
            lastName: user.lastName,
            phone: user.phone || ''
          });
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeForms(): void {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      phone: ['', [Validators.pattern(/^[0-9]{9,}$|^$/)]]
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  private passwordMatchValidator(group: FormGroup): { [key: string]: any } | null {
    const password = group.get('newPassword')?.value;
    const confirm = group.get('confirmPassword')?.value;
    return password === confirm ? null : { passwordMismatch: true };
  }

  loadUserProfile(): void {
    this.isLoading = true;
    this.authService.getCurrentUserProfile()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (user) => {
          this.currentUser = user;
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = 'Error al cargar el perfil';
          this.isLoading = false;
        }
      });
  }

  toggleEditProfile(): void {
    this.isEditingProfile = !this.isEditingProfile;
    this.successMessage = '';
    this.errorMessage = '';
    if (!this.isEditingProfile) {
      this.profileForm.reset();
    }
  }

  toggleChangePassword(): void {
    this.isChangingPassword = !this.isChangingPassword;
    this.successMessage = '';
    this.errorMessage = '';
    if (!this.isChangingPassword) {
      this.passwordForm.reset();
    }
  }

  updateProfile(): void {
    if (this.profileForm.invalid) {
      this.errorMessage = 'Por favor completa todos los campos correctamente';
      return;
    }

    this.isLoading = true;
    const updateDTO: UpdateProfileDTO = this.profileForm.value;

    this.authService.updateProfile(updateDTO)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (user) => {
          this.currentUser = user;
          this.isEditingProfile = false;
          this.successMessage = 'Perfil actualizado exitosamente';
          this.profileForm.reset();
          this.isLoading = false;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Error al actualizar el perfil';
          this.isLoading = false;
        }
      });
  }

  changePassword(): void {
    if (this.passwordForm.invalid) {
      this.errorMessage = 'Por favor completa todos los campos correctamente';
      return;
    }

    this.isLoading = true;
    const changePasswordDTO: ChangePasswordDTO = {
      currentPassword: this.passwordForm.get('currentPassword')?.value,
      newPassword: this.passwordForm.get('newPassword')?.value
    };

    this.authService.changePassword(changePasswordDTO)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.isChangingPassword = false;
          this.successMessage = 'Contraseña cambiada exitosamente';
          this.passwordForm.reset();
          this.isLoading = false;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Error al cambiar la contraseña';
          this.isLoading = false;
        }
      });
  }

  get profileForm_FirstName() {
    return this.profileForm.get('firstName');
  }

  get profileForm_LastName() {
    return this.profileForm.get('lastName');
  }

  get profileForm_Phone() {
    return this.profileForm.get('phone');
  }

  get passwordForm_CurrentPassword() {
    return this.passwordForm.get('currentPassword');
  }

  get passwordForm_NewPassword() {
    return this.passwordForm.get('newPassword');
  }

  get passwordForm_ConfirmPassword() {
    return this.passwordForm.get('confirmPassword');
  }
}
