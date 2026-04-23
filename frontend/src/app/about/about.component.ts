import { Component } from '@angular/core';

@Component({
  selector: 'app-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.css']
})
export class AboutComponent {

  readonly platformName: string = 'Felyxor'; // Nom officiel de la plateforme

  readonly heroTitle: string = 'Turn data into intelligent decisions'; // Promesse centrale de la plateforme

  readonly heroSubtitle: string =
    'Felyxor is an intelligent business decision platform designed to help companies understand performance, detect risks, anticipate change, and act with clarity.'; // Positionnement principal

  readonly visionText: string =
    'Felyxor was created with a simple ambition: transform raw business data into clear, actionable, and intelligent decisions. The platform combines analytics, forecasting, anomaly detection, inventory intelligence, and recommendation engines in one unified workspace.'; // Vision globale du projet

  readonly missionText: string =
    'Our mission is to give organizations a smarter way to monitor activity, understand business signals, reduce blind spots, and make faster operational decisions with confidence.'; // Mission concise et crédible

  readonly startupText: string =
    'More than a dashboard, Felyxor is built as a scalable intelligence platform. Its long-term direction is to evolve into a stronger decision layer for modern organizations, where analytics, machine learning, and operational visibility work together as one product ecosystem.'; // Position startup / évolution future

  readonly pillars: { title: string; description: string }[] = [
    {
      title: 'Business clarity',
      description: 'Centralize key signals into a readable workspace that makes revenue, stock, risks, and trends easier to understand.'
    },
    {
      title: 'Operational intelligence',
      description: 'Move beyond static reporting with anomaly detection, prediction engines, stock forecasting, and business recommendations.'
    },
    {
      title: 'Decision support',
      description: 'Help managers and teams act faster through actionable insights instead of disconnected raw numbers.'
    }
  ]; // Piliers produit

  readonly modules: { title: string; description: string }[] = [
    {
      title: 'Sales analysis',
      description: 'Track revenue, quantities sold, top products, and commercial momentum over time.'
    },
    {
      title: 'Anomaly detection',
      description: 'Detect unusual sales behavior and surface business risks with severity-based prioritization.'
    },
    {
      title: 'Business health',
      description: 'Evaluate overall performance through trend, stability, growth, diversification, and anomaly pressure.'
    },
    {
      title: 'Inventory intelligence',
      description: 'Monitor stock pressure, low-stock signals, and replenishment priorities.'
    },
    {
      title: 'Prediction engines',
      description: 'Forecast revenue and stock pressure to support short-term planning and proactive decisions.'
    },
    {
      title: 'Recommendation layer',
      description: 'Translate analytics into practical actions across products, trends, anomalies, and stock operations.'
    }
  ]; // Modules fonctionnels

  readonly techStack: string[] = [
    'Angular',
    'Spring Boot',
    'FastAPI',
    'PostgreSQL',
    'JWT Authentication',
    'Docker',
    'Machine Learning',
    'REST APIs'
  ]; // Stack technique principale

  readonly values: { title: string; description: string }[] = [
    {
      title: 'Clarity',
      description: 'Insights should simplify decision-making, not add confusion.'
    },
    {
      title: 'Actionability',
      description: 'Every important signal should help the business decide what to do next.'
    },
    {
      title: 'Scalability',
      description: 'The platform is designed to grow from a focused product into a larger intelligence ecosystem.'
    }
  ]; // Valeurs produit / marque
}