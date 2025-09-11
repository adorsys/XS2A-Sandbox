#!/bin/bash

# PSD2 Dynamic Sandbox - Build and Run All Modules Script
# This script builds and runs all backend and frontend modules sequentially
# Author: Generated for psd2-dynamic-sandbox project
# Date: 2025-09-08

set -euo pipefail  # Exit on error, undefined vars, and pipe failures

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Project directories
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOGS_DIR="${PROJECT_ROOT}/logs"
PIDS_DIR="${PROJECT_ROOT}/pids"

# Backend modules (Maven-based)
BACKEND_MODULES=("admin-app" "online-banking" "tpp-app")
# Specific application sub-modules within each backend module
declare -A BACKEND_APP_MODULES=(
    ["admin-app"]="admin-rest-server"
    ["online-banking"]="online-banking-app"
    ["tpp-app"]="tpp-rest-server"
)

# Frontend modules (NPM-based)
FRONTEND_MODULES=("admin-ui" "developer-portal-ui" "oba-ui" "tpp-ui")

# Function to print colored output
print_status() {
    local level=$1
    local message=$2
    case $level in
        "INFO")  echo -e "${BLUE}[INFO]${NC} $message" ;;
        "SUCCESS") echo -e "${GREEN}[SUCCESS]${NC} $message" ;;
        "WARNING") echo -e "${YELLOW}[WARNING]${NC} $message" ;;
        "ERROR") echo -e "${RED}[ERROR]${NC} $message" ;;
    esac
}

# Function to check if required tools are available
check_prerequisites() {
    print_status "INFO" "Checking prerequisites..."

    local missing_tools=()

    if ! command -v mvn &> /dev/null; then
        missing_tools+=("mvn (Maven)")
    fi

    if ! command -v java &> /dev/null; then
        missing_tools+=("java")
    fi

    if ! command -v npm &> /dev/null; then
        missing_tools+=("npm")
    fi

    if [ ${#missing_tools[@]} -gt 0 ]; then
        print_status "ERROR" "Missing required tools:"
        for tool in "${missing_tools[@]}"; do
            echo "  - $tool"
        done
        print_status "ERROR" "Please install the missing tools and try again."
        exit 1
    fi

    print_status "SUCCESS" "All prerequisites are available."
}

# Function to setup directories
setup_directories() {
    print_status "INFO" "Setting up directories..."

    # Create logs directory if it doesn't exist
    if [ ! -d "$LOGS_DIR" ]; then
        mkdir -p "$LOGS_DIR"
        print_status "INFO" "Created logs directory: $LOGS_DIR"
    fi

    # Create pids directory if it doesn't exist
    if [ ! -d "$PIDS_DIR" ]; then
        mkdir -p "$PIDS_DIR"
        print_status "INFO" "Created pids directory: $PIDS_DIR"
    fi

    # Clean up old log files and PID files
    rm -f "$LOGS_DIR"/*.log
    rm -f "$PIDS_DIR"/*.pid
    rm -f "$PIDS_DIR"/all-pids

    print_status "SUCCESS" "Directories setup complete."
}

# Function to build a Maven module
build_maven_module() {
    local module=$1
    local module_dir="${PROJECT_ROOT}/${module}"

    print_status "INFO" "Building Maven module: $module"

    if [ ! -d "$module_dir" ]; then
        print_status "ERROR" "Module directory not found: $module_dir"
        return 1
    fi

    cd "$module_dir"

    # Use Maven settings if available (as seen in Makefile)
    local mvn_cmd="mvn clean install"
    if [ -f "${PROJECT_ROOT}/scripts/mvn-release-settings.xml" ]; then
        mvn_cmd="mvn -ntp --settings ${PROJECT_ROOT}/scripts/mvn-release-settings.xml clean install"
    fi

    if ! $mvn_cmd > "${LOGS_DIR}/${module}-build.log" 2>&1; then
        print_status "ERROR" "Failed to build Maven module: $module"
        print_status "ERROR" "Check logs at: ${LOGS_DIR}/${module}-build.log"
        return 1
    fi

    print_status "SUCCESS" "Successfully built Maven module: $module"
    return 0
}

# Function to start a backend application
start_backend_app() {
    local module=$1
    local app_module=${BACKEND_APP_MODULES[$module]}
    local app_dir="${PROJECT_ROOT}/${module}/${app_module}"

    print_status "INFO" "Starting backend application: $module ($app_module)"

    if [ ! -d "$app_dir" ]; then
        print_status "ERROR" "Application directory not found: $app_dir"
        return 1
    fi

    cd "$app_dir"

    # Look for JAR file in target directory
    local jar_file=$(find target -name "*.jar" -not -name "*sources.jar" -not -name "*javadoc.jar" 2>/dev/null | head -1)

    if [ -n "$jar_file" ] && [ -f "$jar_file" ]; then
        print_status "INFO" "Found JAR file: $jar_file - starting with java -jar"
        java -jar "$jar_file" > "${LOGS_DIR}/${module}.log" 2>&1 &
        local pid=$!
    else
        print_status "WARNING" "No JAR file found for $module, falling back to mvn spring-boot:run"
        mvn spring-boot:run > "${LOGS_DIR}/${module}.log" 2>&1 &
        local pid=$!
    fi

    # Save PID
    echo "$pid" > "${PIDS_DIR}/${module}.pid"

    # Wait a moment and check if process is still running
    sleep 3
    if ! kill -0 "$pid" 2>/dev/null; then
        print_status "ERROR" "Backend application $module failed to start or crashed immediately"
        print_status "ERROR" "Check logs at: ${LOGS_DIR}/${module}.log"
        return 1
    fi

    print_status "SUCCESS" "Successfully started backend application: $module (PID: $pid)"
    return 0
}

# Function to build a frontend module
build_frontend_module() {
    local module=$1
    local module_dir="${PROJECT_ROOT}/${module}"

    print_status "INFO" "Building frontend module: $module"

    if [ ! -d "$module_dir" ]; then
        print_status "ERROR" "Module directory not found: $module_dir"
        return 1
    fi

    cd "$module_dir"

    if ! npm ci > "${LOGS_DIR}/${module}-build.log" 2>&1; then
        print_status "ERROR" "Failed to run npm ci for frontend module: $module"
        print_status "ERROR" "Check logs at: ${LOGS_DIR}/${module}-build.log"
        return 1
    fi

    print_status "SUCCESS" "Successfully built frontend module: $module"
    return 0
}

# Function to start a frontend application
start_frontend_app() {
    local module=$1
    local module_dir="${PROJECT_ROOT}/${module}"

    print_status "INFO" "Starting frontend application: $module"

    cd "$module_dir"

    npm start > "${LOGS_DIR}/${module}.log" 2>&1 &
    local pid=$!

    # Save PID
    echo "$pid" > "${PIDS_DIR}/${module}.pid"

    # Wait a moment and check if process is still running
    sleep 5
    if ! kill -0 "$pid" 2>/dev/null; then
        print_status "ERROR" "Frontend application $module failed to start or crashed immediately"
        print_status "ERROR" "Check logs at: ${LOGS_DIR}/${module}.log"
        return 1
    fi

    print_status "SUCCESS" "Successfully started frontend application: $module (PID: $pid)"
    return 0
}

# Function to cleanup on script exit
cleanup() {
    print_status "INFO" "Script interrupted. Cleaning up..."

    # Kill all started processes
    for pid_file in "$PIDS_DIR"/*.pid; do
        if [ -f "$pid_file" ]; then
            local pid=$(cat "$pid_file")
            local module=$(basename "$pid_file" .pid)
            if kill -0 "$pid" 2>/dev/null; then
                print_status "INFO" "Stopping $module (PID: $pid)"
                kill "$pid" 2>/dev/null || true
                sleep 2
                # Force kill if still running
                if kill -0 "$pid" 2>/dev/null; then
                    kill -9 "$pid" 2>/dev/null || true
                fi
            fi
            rm -f "$pid_file"
        fi
    done

    exit 1
}

# Main execution function
main() {
    print_status "INFO" "Starting PSD2 Dynamic Sandbox build and run script..."
    print_status "INFO" "Project root: $PROJECT_ROOT"

    # Setup signal handlers for cleanup
    trap cleanup SIGINT SIGTERM

    # Check prerequisites
    check_prerequisites

    # Setup directories
    setup_directories

    cd "$PROJECT_ROOT"

    # Build and start backend modules sequentially
    print_status "INFO" "Processing backend modules..."
    for module in "${BACKEND_MODULES[@]}"; do
        print_status "INFO" "Processing backend module: $module"

        if ! build_maven_module "$module"; then
            print_status "ERROR" "Failed to build backend module: $module"
            exit 1
        fi

        if ! start_backend_app "$module"; then
            print_status "ERROR" "Failed to start backend application: $module"
            exit 1
        fi

        print_status "SUCCESS" "Backend module $module is running"
        echo
    done

    # Build and start frontend modules sequentially
    print_status "INFO" "Processing frontend modules..."
    for module in "${FRONTEND_MODULES[@]}"; do
        print_status "INFO" "Processing frontend module: $module"

        if ! build_frontend_module "$module"; then
            print_status "ERROR" "Failed to build frontend module: $module"
            exit 1
        fi

        if ! start_frontend_app "$module"; then
            print_status "ERROR" "Failed to start frontend application: $module"
            exit 1
        fi

        print_status "SUCCESS" "Frontend module $module is running"
        echo
    done

    # All modules started successfully
    print_status "SUCCESS" "All modules have been built and started successfully!"
    print_status "INFO" "Backend modules: ${BACKEND_MODULES[*]}"
    print_status "INFO" "Frontend modules: ${FRONTEND_MODULES[*]}"
    print_status "INFO" "Logs are available in: $LOGS_DIR"
    print_status "INFO" "Process PIDs are saved in: $PIDS_DIR"

    # Create consolidated all-pids file with all PIDs on a single line
    local all_pids=""
    for pid_file in "$PIDS_DIR"/*.pid; do
        if [ -f "$pid_file" ] && [ "$(basename "$pid_file")" != "all-pids" ]; then
            local pid=$(cat "$pid_file" 2>/dev/null || echo "")
            if [ -n "$pid" ]; then
                if [ -z "$all_pids" ]; then
                    all_pids="$pid"
                else
                    all_pids="$all_pids $pid"
                fi
            fi
        fi
    done

    if [ -n "$all_pids" ]; then
        echo "$all_pids" > "${PIDS_DIR}/all-pids"
        print_status "INFO" "All PIDs consolidated in: ${PIDS_DIR}/all-pids"
    fi

    echo
    print_status "INFO" "To stop all services, you can kill the processes using the PID files in $PIDS_DIR"
    print_status "INFO" "Or create a companion stop script to gracefully shut down all services."
    print_status "INFO" "All processes are running in the background. Monitor logs for any issues."
}

# Run main function
main "$@"
