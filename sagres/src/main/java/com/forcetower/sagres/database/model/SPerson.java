/*
 * Copyright (c) 2019.
 * João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This file is part of the UNES Open Source Project.
 *
 * UNES is licensed under the MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.forcetower.sagres.database.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(indices = {
    @Index(value = "sagres_id", unique = true),
    @Index(value = "cpf"),
    @Index(value = "email")
})
public class SPerson {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @SerializedName(value = "nome")
    private String name;
    @SerializedName(value = "nomeExibicao")
    private String exhibitionName;
    private String cpf;
    private String email;
    @ColumnInfo(name = "sagres_id")
    @Nullable
    private String sagresId;
    private boolean mocked;

    public SPerson(long id, String name, String exhibitionName, String cpf, String email) {
        this.id = id;
        this.name = name;
        this.exhibitionName = exhibitionName;
        this.cpf = cpf;
        this.email = email;
        this.mocked = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        name = name.trim();
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExhibitionName() {
        return exhibitionName;
    }

    public void setExhibitionName(String exhibitionName) {
        this.exhibitionName = exhibitionName;
    }

    public String getCpf() {
        if (cpf == null) return null;

        cpf = cpf.trim();
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    @NonNull
    public String toString() {
        return "ID: " + getId() + " - Name: " + getName();
    }

    @Nullable
    public String getSagresId() {
        return sagresId;
    }

    public void setSagresId(@Nullable String sagresId) {
        this.sagresId = sagresId;
    }

    public String getUnique() {
        return cpf.toLowerCase() + ".." + id;
    }

    public boolean isMocked() {
        return mocked;
    }

    public void setMocked(boolean mocked) {
        this.mocked = mocked;
    }
}
