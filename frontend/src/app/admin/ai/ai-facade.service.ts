import { Injectable } from '@angular/core';
import { Observable, of, map } from 'rxjs';
import { AIResponse } from 'src/app/models/ai-response.model';
import { AiService } from './ai.service';

@Injectable({
  providedIn: 'root'
})

//Ce service met en cache le gros AIResponse
//les pages overview / anomalies / health / inventory / ... lisent ensuite dans ce cache
//donc on évite de retaper le gros endpoint à chaque navigation
export class AiFacadeService {

  private analysisCache: AIResponse | null = null;

  constructor(private aiService: AiService) {}

  getFullAnalysis(): Observable<AIResponse> {
    if (this.analysisCache) {
      return of(this.analysisCache);
    }

    return this.aiService.getAnalysis().pipe(
      map(response => {
        this.analysisCache = response.data;
        return response.data;
      })
    );
  }

  clearCache(): void {
    this.analysisCache = null;
  }
}