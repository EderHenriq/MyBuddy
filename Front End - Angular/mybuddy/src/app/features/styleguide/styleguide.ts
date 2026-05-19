import { Component } from '@angular/core';
import { InputText } from 'primeng/inputtext';
import { Checkbox } from 'primeng/checkbox';
import { Button } from 'primeng/button';
import { FormsModule } from '@angular/forms';
import { EmptyState } from '../../shared/components/empty-state/empty-state';

@Component({
  selector: 'app-styleguide',
  imports: [InputText, Checkbox, Button, FormsModule, EmptyState],
  templateUrl: './styleguide.html',
  styleUrl: './styleguide.scss',
})
export class Styleguide {
  checked = false;
}
