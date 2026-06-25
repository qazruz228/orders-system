{{/*
Expand the name of the chart.
*/}}
{{- define "order-service.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "order-service.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "order-service.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels.
*/}}
{{- define "order-service.labels" -}}
helm.sh/chart: {{ include "order-service.chart" . }}
{{ include "order-service.selectorLabels" . }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: order-system
{{- end -}}

{{/*
Selector labels.
*/}}
{{- define "order-service.selectorLabels" -}}
app.kubernetes.io/name: {{ include "order-service.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/component: order-service
{{- end -}}

{{/*
Create the name of the service account to use.
*/}}
{{- define "order-service.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
{{- default (include "order-service.fullname" .) .Values.serviceAccount.name -}}
{{- else -}}
{{- default "default" .Values.serviceAccount.name -}}
{{- end -}}
{{- end -}}

{{/*
Create the datasource secret name.
*/}}
{{- define "order-service.datasourceSecretName" -}}
{{- default (printf "%s-datasource" (include "order-service.fullname" .)) .Values.config.datasource.existingSecret -}}
{{- end -}}

{{/*
Return Kafka bootstrap servers, preferring the service value over global.
*/}}
{{- define "order-service.kafkaBootstrapServers" -}}
{{- $global := default dict .Values.global -}}
{{- $globalKafka := default dict $global.kafka -}}
{{- default $globalKafka.bootstrapServers .Values.config.kafka.bootstrapServers -}}
{{- end -}}
