#!/usr/bin/env bash
[[ -r "$HOME/.nvm/nvm.sh" ]] && source "$HOME/.nvm/nvm.sh"
cdt2 serve --proxy-config proxy.config.json
