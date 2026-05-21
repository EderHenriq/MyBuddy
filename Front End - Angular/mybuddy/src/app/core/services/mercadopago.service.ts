import { Injectable } from "@angular/core";
import { environment } from '../../../environments/environment';

declare const MercadoPago: any;

@Injectable({
    providedIn: 'root'
})

export class MercadoPagoService {
    
    private mp: any = null;

    async initialize(): Promise<void> {
        if (this.mp) return;
    
        await this.loadScript();

        this.mp = new MercadoPago(environment.mercadoPagoPublicKey, {
            locale: 'es-AR'
        });
    }

    private loadScript(): Promise<void> {
        return new Promise((resolve, reject) => {
            if (document.getElementById('mp-sdk')) {
                resolve();
                return;
            }

            const script = document.createElement('script');
            script.id = 'mp-sdk';
            script.src = 'https://sdk.mercadopago.com/js/v2';
            script.onload = () => resolve();
            script.onerror = () => reject(new Error('Failed to load MercadoPago SDK'));
            document.head.appendChild(script);
        });
    }
    getInstance(): any {
        return this.mp;
    }
}