{{/*
Expand the name of the chart.
*/}}
{{- define "order-system.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "order-system.fullname" -}}
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
{{- define "order-system.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels.
*/}}
{{- define "order-system.labels" -}}
helm.sh/chart: {{ include "order-system.chart" . }}
app.kubernetes.io/name: {{ include "order-system.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: order-system
{{- end -}}

{{/*
Create a Kubernetes-safe KafkaTopic resource name.
*/}}
{{- define "order-system.kafkaTopicResourceName" -}}
{{- $root := .root -}}
{{- $topic := .topic -}}
{{- $rawName := default $topic.name $topic.resourceName -}}
{{- printf "%s-%s" (include "order-system.fullname" $root) $rawName | lower | replace "_" "-" | replace "." "-" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Return the Strimzi Kafka cluster name used by Kafka and KafkaTopic resources.
*/}}
{{- define "order-system.kafkaClusterName" -}}
{{- default "order-system-kafka" .Values.kafka.name -}}
{{- end -}}
