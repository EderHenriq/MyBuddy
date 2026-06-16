import { Component, OnInit, inject, signal } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { Footer } from '@shared/components/footer/footer';
import { PetService } from '@core/services/pet.service';
import { CardPetComponent } from '@shared/components/card-pet/card-pet.component';
import { HeroSectionComponent } from '@shared/components/hero-section/hero-section.component';
