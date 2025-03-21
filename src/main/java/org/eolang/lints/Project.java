/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.Collection;

/**
 * Project, files of which can be analyzed separately or together.
 *
 * @since 0.0.9
 */
public interface Project {

    /**
     * Defects found by analyzing each file independently.
     * @return Collection of defects from all files
     */
    Collection<Defect> singleDefects();

    /**
     * Defects found by analyzing all files together.
     * @return Collection of defects from all files
     */
    Collection<Defect> wpaDefects();
}
