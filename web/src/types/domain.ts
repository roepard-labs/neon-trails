export type ActorId = string

export interface Actor {
  id: ActorId
  nombre: string
  descripcion: string
  responsabilidades: string[]
  icon: string
}

export type RequirementCategory =
  | 'negocio'
  | 'usuario'
  | 'sistema'
  | 'funcional'
  | 'no-funcional'

export interface Requirement {
  codigo: string
  nombre: string
  descripcion: string
}

export interface RequirementGroup {
  categoria: RequirementCategory
  titulo: string
  subtitulo: string
  items: Requirement[]
}

export type UseCaseType = 'principal' | 'secundario'

export interface UseCase {
  codigo: string
  nombre: string
  actores: ActorId[]
  tipo: UseCaseType
  precondiciones: string[]
  descripcion: string
  flujoNormal: string[]
  flujosAlternos: { titulo: string; pasos: string[] }[]
  excepciones: { titulo: string; descripcion: string }[]
  postcondiciones: string[]
}

export type UseCaseRelation = 'include' | 'extend'

export interface UseCaseDiagramNode {
  id: string
  label: string
}

export interface UseCaseDiagramRelation {
  from: string
  to: string
  type: 'asociacion' | UseCaseRelation
}

export interface ActivityDiagram {
  codigoCU: string
  titulo: string
  descripcion: string
  mermaid: string
}

export interface MockupScreen {
  id: string
  titulo: string
  descripcion: string
  frame: 'desktop' | 'mobile'
  component: string
}

export interface Integrante {
  nombre: string
  identificacion?: string
}

export interface ProjectInfo {
  titulo: string
  subtitulo: string
  asignatura: string
  carrera: string
  universidad: string
  ciudad: string
  ano: number
  integrantes: Integrante[]
  docente?: string
}
